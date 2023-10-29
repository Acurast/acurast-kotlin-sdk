package acurast.codec.extensions

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

public enum class CompactMode(public val value: Byte) {
    One(0b00),
    Two(0b01),
    Four(0b10),
    Big(0b11);
}

public fun Long.compactMode(): CompactMode {
    return if (this < 0) {
        throw ScaleEncoderException("Negative numbers are not supported")
    } else if (this <= 0x3f) {
        CompactMode.One
    } else if (this <= 0x3fff) {
        CompactMode.Two
    } else if (this <= 0x3fffffff) {
        CompactMode.Four
    } else {
        CompactMode.Big
    }
}

public fun Long.toU8a(): ByteArray = ByteBuffer.allocate(8)
    .order(ByteOrder.LITTLE_ENDIAN)
    .putLong(this)
    .array()

public fun Long.toCompactU8a(): ByteArray {
    val mode = this.compactMode()
    var compact = (this shl 2) + mode.value

    var nBytes = when(mode) {
        CompactMode.One -> 1
        CompactMode.Two -> 2
        CompactMode.Four -> 4
        CompactMode.Big -> return this.toBigInteger().toCompactU8a()
    }

    val ret = ByteBuffer.allocate(nBytes)
    ret.order(ByteOrder.LITTLE_ENDIAN)
    while (nBytes > 0) {
        ret.put(compact.toByte() and 0xff.toByte())
        compact = compact shr 8
        nBytes -= 1
    }
    return ret.array()
}