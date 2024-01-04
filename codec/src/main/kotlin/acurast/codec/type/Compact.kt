package acurast.codec.type

import acurast.codec.extensions.ScaleEncoderException
import java.nio.ByteBuffer

public enum class CompactMode(public val value: Byte) {
    One(0b00),
    Two(0b01),
    Four(0b10),
    Big(0b11);

    public companion object {
        public fun read(buffer: ByteBuffer): CompactMode {
            return when (buffer.get()) {
                One.value -> One
                Two.value -> Two
                Four.value -> Four
                Big.value -> Big
                else -> throw ScaleEncoderException("Unknown compact mode")
            }
        }
    }
}

public data class Compact<T: ToCompactU8a>(val x: T): ToU8a {
    override fun toU8a(): ByteArray {
        return x.toCompactU8a()
    }
}