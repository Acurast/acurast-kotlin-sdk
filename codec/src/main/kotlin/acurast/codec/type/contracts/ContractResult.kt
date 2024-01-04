package acurast.codec.type.contracts

import acurast.codec.extensions.*
import acurast.codec.type.ToU8a
import acurast.codec.type.UInt128
import acurast.codec.type.Weight
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer

public sealed interface StorageDeposit : ToU8a {
    public enum class Tag(public val id: Byte): ToU8a {
        Refund(0),
        Charge(1);

        override fun toU8a(): ByteArray = id.toU8a()
    }

    public sealed interface Kind {
        public val tag: Tag

        public companion object {
            internal val values: List<Kind>
                get() = listOf(
                    Refund,
                    Charge
                )
        }
    }

    public object Decoder {
        public inline fun read(buffer: ByteBuffer, optionalParser: ByteBuffer.() -> ToU8a): StorageDeposit = buffer.littleEndian {
            return when (val tag = buffer.readByte()) {
                Tag.Refund.id -> Refund(optionalParser(buffer))
                Tag.Charge.id -> Charge(optionalParser(buffer))
                else -> throw UnsupportedEncodingException("Unknown option tag: $tag")
            }
        }
    }

    public data class Refund(private val inner: ToU8a) : StorageDeposit  {
        public companion object : Kind {
            override val tag: Tag = Tag.Refund
        }

        override fun toU8a(): ByteArray = tag.toU8a() + inner.toU8a()
    }

    public data class Charge(private val inner: ToU8a) : StorageDeposit  {
        public companion object : Kind {
            override val tag: Tag = Tag.Charge
        }

        override fun toU8a(): ByteArray = tag.toU8a() + inner.toU8a()
    }
}

public data class ExecReturnValue(
    public val flags: UInt,
    public val data: ByteArray
): ToU8a {
    public companion object {
        public fun read(buffer: ByteBuffer): ExecReturnValue {
            return ExecReturnValue(
                flags = buffer.readU32(),
                data = buffer.readByteArray()
            )
        }
    }

    override fun toU8a(): ByteArray = flags.toInt().toU8a() + data.toU8a()
}

public data class DispatchError(
    public val kind: Byte
) {
    public companion object {
        public fun read(buffer: ByteBuffer): DispatchError {
            return DispatchError(buffer.get())
        }
    }
}

public data class ContractResult(
    public val gas_consumed: Weight,
    public val gas_required: Weight,
    public val storage_deposit: StorageDeposit,
    public val debug_message: ByteArray,
    public val result: acurast.codec.type.Result<ExecReturnValue, DispatchError>,
    public val events: List<ByteArray>,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): ContractResult {
            val gasConsumed = Weight.read(buffer)
            val gasRequired = Weight.read(buffer)
            return ContractResult(
                gas_consumed = gasConsumed,
                gas_required = gasRequired,
                storage_deposit = StorageDeposit.Decoder.read(buffer) { UInt128(readU128()) },
                debug_message = buffer.readByteArray(),
                result = acurast.codec.type.Result.Decoder.read(buffer, { ExecReturnValue.read(it) } , { DispatchError.read(it) }),
                events = emptyList() // TODO: parse events if necessary
            )
        }
    }
}