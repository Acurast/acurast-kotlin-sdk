package acurast.codec.metadata

import acurast.codec.extensions.ScaleDecoderException
import acurast.codec.extensions.readBoolean
import acurast.codec.extensions.readByteArray
import acurast.codec.extensions.readCompactInt
import acurast.codec.extensions.readCompactInteger
import acurast.codec.extensions.readSigned
import acurast.codec.extensions.readString
import acurast.codec.extensions.readU8
import acurast.codec.extensions.readUnsigned
import acurast.codec.metadata.RuntimeMetadata.PrimitiveKind
import acurast.codec.metadata.RuntimeMetadata.TypeDef
import java.math.BigInteger
import java.nio.ByteBuffer

/** A call decoded with the runtime metadata: `pallet.call(arguments)`. */
public class DecodedCall(public val pallet: String, public val name: String, public val arguments: List<Argument>) {
    /** The call in polkadot-js notation, e.g. `balances.transferKeepAlive`. */
    public val id: String
        get() = pallet.replaceFirstChar { it.lowercaseChar() } + "." + name.snakeToCamelCase()

    /** This call and every call nested in its arguments (e.g. the calls of a batch), depth-first. */
    public fun allCalls(): List<DecodedCall> =
        listOf(this) + arguments.flatMap { argument -> argument.directCalls().flatMap { it.allCalls() } }
}

public class Argument(public val name: String?, public val typeName: String?, public val value: Value) {
    /** The calls this argument holds, e.g. the calls of a batch, without the calls nested in those. */
    public fun directCalls(): List<DecodedCall> = value.directCalls()
}

public sealed interface Value {
    public data class Bool(val value: Boolean) : Value
    public data class Number(val value: BigInteger) : Value
    public data class Text(val value: String) : Value
    public class Bytes(public val value: ByteArray) : Value
    public class Account(public val accountId: ByteArray) : Value
    public class Composite(public val fields: List<Argument>) : Value
    public class Variant(public val name: String, public val fields: List<Argument>) : Value
    public class Sequence(public val items: List<Value>) : Value
    public class Call(public val call: DecodedCall) : Value
}

/**
 * Decodes the SCALE-encoded call of a transaction with [metadata].
 *
 * Throws unless the call decodes exactly, consuming every byte.
 */
public fun decodeCall(method: ByteArray, metadata: RuntimeMetadata): DecodedCall {
    val buffer = ByteBuffer.wrap(method)
    val call = CallDecoder(metadata, buffer).decodeCall(depth = 0)
    if (buffer.hasRemaining()) throw ScaleDecoderException("Trailing call data: ${buffer.remaining()} bytes")

    return call
}

private fun String.snakeToCamelCase(): String =
    split("_").mapIndexed { index, part -> if (index == 0) part else part.replaceFirstChar { it.uppercaseChar() } }.joinToString("")

private fun Value.directCalls(): List<DecodedCall> = when (this) {
    is Value.Call -> listOf(call)
    is Value.Composite -> fields.flatMap { it.directCalls() }
    is Value.Variant -> fields.flatMap { it.directCalls() }
    is Value.Sequence -> items.flatMap { it.directCalls() }
    else -> emptyList()
}

private class CallDecoder(private val metadata: RuntimeMetadata, private val buffer: ByteBuffer) {

    fun decodeCall(depth: Int): DecodedCall {
        val index = buffer.readU8().toInt()
        val pallet = metadata.pallet(index) ?: fail("Unknown pallet $index")
        val callType = pallet.callType ?: fail("Pallet ${pallet.name} has no calls")
        val variant = decodeVariant(callType, depth)

        return DecodedCall(pallet.name, variant.name, variant.fields)
    }

    private fun decode(typeId: Int, depth: Int): Value {
        if (depth > MAX_DEPTH) fail("Nesting too deep")
        val type = metadata.type(typeId)
        if (type.path.lastOrNull() == RUNTIME_CALL) return Value.Call(decodeCall(depth + 1))

        return when (val def = type.def) {
            is TypeDef.Composite -> decodeComposite(type, def, depth)
            is TypeDef.VariantType -> decodeVariant(typeId, depth)
            is TypeDef.Sequence -> decodeSequence(buffer.readCompactInt(), def.type, depth)
            is TypeDef.Array -> decodeSequence(def.length.toInt(), def.type, depth)
            is TypeDef.Tuple -> Value.Composite(def.types.map { Argument(null, null, decode(it, depth + 1)) })
            is TypeDef.Primitive -> decodePrimitive(def.kind)
            is TypeDef.Compact -> Value.Number(buffer.readCompactInteger())
            is TypeDef.BitSequence -> fail("Bit sequences are not supported")
        }
    }

    private fun decodeComposite(type: RuntimeMetadata.Type, def: TypeDef.Composite, depth: Int): Value {
        val fields = decodeFields(def.fields, depth)
        val single = fields.singleOrNull()?.value

        return when {
            type.path.lastOrNull() == ACCOUNT_ID && single is Value.Bytes -> Value.Account(single.value)
            else -> Value.Composite(fields)
        }
    }

    private fun decodeVariant(typeId: Int, depth: Int): Value.Variant {
        val def = metadata.type(typeId).def as? TypeDef.VariantType ?: fail("Type $typeId is not an enum")
        val index = buffer.readU8().toInt()
        val variant = def.variants.firstOrNull { it.index == index } ?: fail("Unknown variant $index of type $typeId")

        return Value.Variant(variant.name, decodeFields(variant.fields, depth))
    }

    private fun decodeFields(fields: List<RuntimeMetadata.Field>, depth: Int): List<Argument> =
        fields.map { Argument(it.name, it.typeName, decode(it.type, depth + 1)) }

    private fun decodeSequence(length: Int, elementType: Int, depth: Int): Value {
        if (length < 0) fail("Invalid sequence length")
        val element = metadata.type(elementType).def
        if (element is TypeDef.Primitive && element.kind == PrimitiveKind.U8) return Value.Bytes(buffer.readByteArray(length))
        if (length > buffer.remaining()) fail("Sequence longer than the remaining data")

        return Value.Sequence(List(length) { decode(elementType, depth + 1) })
    }

    private fun decodePrimitive(kind: PrimitiveKind): Value = when (kind) {
        PrimitiveKind.Bool -> Value.Bool(buffer.readBoolean())
        PrimitiveKind.Str -> Value.Text(buffer.readString())
        PrimitiveKind.Char -> Value.Text(String(Character.toChars(buffer.readUnsigned(kind.byteCount).toInt())))
        else -> Value.Number(if (kind.isSigned) buffer.readSigned(kind.byteCount) else buffer.readUnsigned(kind.byteCount))
    }

    private fun fail(reason: String): Nothing = throw ScaleDecoderException(reason)

    companion object {
        private const val MAX_DEPTH = 64
        private const val RUNTIME_CALL = "RuntimeCall"
        private const val ACCOUNT_ID = "AccountId32"
    }
}
