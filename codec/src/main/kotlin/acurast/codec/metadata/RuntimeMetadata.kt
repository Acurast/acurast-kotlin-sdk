package acurast.codec.metadata

import acurast.codec.extensions.ScaleDecoderException
import acurast.codec.extensions.readByteArray
import acurast.codec.extensions.readCompactInt
import acurast.codec.extensions.readList
import acurast.codec.extensions.readOptional
import acurast.codec.extensions.readString
import acurast.codec.extensions.readU32
import acurast.codec.extensions.readU8
import java.nio.ByteBuffer

/**
 * The parts of a runtime's metadata (V14 or V15) needed to decode calls: the type registry and
 * the pallets with their call types.
 */
public class RuntimeMetadata(
    private val types: Map<Int, Type>,
    public val pallets: List<Pallet>,
) {
    public fun type(id: Int): Type = types[id] ?: throw ScaleDecoderException("Unknown type $id")

    public fun pallet(index: Int): Pallet? = pallets.firstOrNull { it.index == index }

    public class Pallet(public val name: String, public val index: Int, public val callType: Int?)

    public class Type(public val path: List<String>, public val def: TypeDef)

    public class Field(public val name: String?, public val type: Int, public val typeName: String?)

    public class Variant(public val name: String, public val index: Int, public val fields: List<Field>)

    public sealed interface TypeDef {
        public class Composite(public val fields: List<Field>) : TypeDef
        public class VariantType(public val variants: List<Variant>) : TypeDef
        public class Sequence(public val type: Int) : TypeDef
        public class Array(public val length: Long, public val type: Int) : TypeDef
        public class Tuple(public val types: List<Int>) : TypeDef
        public class Primitive(public val kind: PrimitiveKind) : TypeDef
        public class Compact(public val type: Int) : TypeDef
        public class BitSequence(public val storeType: Int, public val orderType: Int) : TypeDef
    }

    public enum class PrimitiveKind(public val byteCount: Int, public val isSigned: Boolean = false) {
        Bool(1), Char(4), Str(0),
        U8(1), U16(2), U32(4), U64(8), U128(16), U256(32),
        I8(1, true), I16(2, true), I32(4, true), I64(8, true), I128(16, true), I256(32, true),
    }

    public companion object {
        /** Decodes the `RuntimeMetadataPrefixed` returned by `state_getMetadata`. */
        public fun decode(bytes: ByteArray): RuntimeMetadata = ByteBuffer.wrap(bytes).readRuntimeMetadata()
    }
}

private const val METADATA_MAGIC = 0x6174656du // "meta", little-endian
private val SUPPORTED_METADATA_VERSIONS = setOf(14, 15)

public fun ByteBuffer.readRuntimeMetadata(): RuntimeMetadata {
    if (readU32() != METADATA_MAGIC) throw ScaleDecoderException("Not runtime metadata")
    val version = readU8().toInt()
    if (version !in SUPPORTED_METADATA_VERSIONS) throw ScaleDecoderException("Unsupported metadata version $version")

    val types = readList { readPortableType() }.toMap()
    val pallets = readList { readPallet(withDocs = version >= 15) }

    return RuntimeMetadata(types, pallets)
}

private fun ByteBuffer.readPortableType(): Pair<Int, RuntimeMetadata.Type> {
    val id = readCompactInt()
    val path = readList { readString() }
    readList { readString() to readOptional { readCompactInt() } } // type parameters
    val def = readTypeDef()
    skipDocs()

    return id to RuntimeMetadata.Type(path, def)
}

private fun ByteBuffer.readTypeDef(): RuntimeMetadata.TypeDef = when (val kind = readU8().toInt()) {
    0 -> RuntimeMetadata.TypeDef.Composite(readList { readField() })
    1 -> RuntimeMetadata.TypeDef.VariantType(readList { readVariant() })
    2 -> RuntimeMetadata.TypeDef.Sequence(readCompactInt())
    3 -> RuntimeMetadata.TypeDef.Array(readU32().toLong(), readCompactInt())
    4 -> RuntimeMetadata.TypeDef.Tuple(readList { readCompactInt() })
    5 -> RuntimeMetadata.TypeDef.Primitive(readPrimitiveKind())
    6 -> RuntimeMetadata.TypeDef.Compact(readCompactInt())
    7 -> RuntimeMetadata.TypeDef.BitSequence(readCompactInt(), readCompactInt())
    else -> throw ScaleDecoderException("Unknown type definition $kind")
}

private fun ByteBuffer.readPrimitiveKind(): RuntimeMetadata.PrimitiveKind {
    val index = readU8().toInt()
    return RuntimeMetadata.PrimitiveKind.entries.getOrNull(index) ?: throw ScaleDecoderException("Unknown primitive $index")
}

private fun ByteBuffer.readField(): RuntimeMetadata.Field {
    val name = readOptional { readString() }
    val type = readCompactInt()
    val typeName = readOptional { readString() }
    skipDocs()

    return RuntimeMetadata.Field(name, type, typeName)
}

private fun ByteBuffer.readVariant(): RuntimeMetadata.Variant {
    val name = readString()
    val fields = readList { readField() }
    val index = readU8().toInt()
    skipDocs()

    return RuntimeMetadata.Variant(name, index, fields)
}

private fun ByteBuffer.readPallet(withDocs: Boolean): RuntimeMetadata.Pallet {
    val name = readString()
    readOptional { skipStorage() }
    val callType = readOptional { readCompactInt() }
    readOptional { readCompactInt() } // event type
    readList { skipConstant() }
    readOptional { readCompactInt() } // error type
    val index = readU8().toInt()
    if (withDocs) skipDocs()

    return RuntimeMetadata.Pallet(name, index, callType)
}

private fun ByteBuffer.skipStorage() {
    readString() // prefix
    readList { skipStorageEntry() }
}

private fun ByteBuffer.skipStorageEntry() {
    readString() // name
    readU8() // modifier
    when (val kind = readU8().toInt()) {
        0 -> readCompactInt()
        1 -> {
            readList { readU8() } // hashers
            readCompactInt() // key
            readCompactInt() // value
        }
        else -> throw ScaleDecoderException("Unknown storage entry type $kind")
    }
    readByteArray() // default value
    skipDocs()
}

private fun ByteBuffer.skipConstant() {
    readString() // name
    readCompactInt() // type
    readByteArray() // value
    skipDocs()
}

private fun ByteBuffer.skipDocs() {
    readList { readString() }
}
