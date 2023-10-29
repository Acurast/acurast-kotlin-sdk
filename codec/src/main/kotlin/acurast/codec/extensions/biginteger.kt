package acurast.codec.extensions

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

private val MAX = BigInteger.valueOf(2).pow(536).subtract(BigInteger.ONE)

public fun BigInteger.compactMode(): CompactMode {
    return if (this.signum() < 0) {
        throw ScaleEncoderException("Negative numbers are not supported")
    } else if (this > MAX) {
        throw ScaleEncoderException("Numbers larger than 2**536-1 are not supported")
    } else if (this == BigInteger.ZERO) {
        CompactMode.One
    } else if (this > BigInteger.valueOf(0x3fffffff)) {
        CompactMode.Big
    } else if (this > BigInteger.valueOf(0x3fff)) {
        CompactMode.Four
    } else if (this > BigInteger.valueOf(0x3f)) {
        CompactMode.Two
    } else {
        CompactMode.Big
    }
}

public fun BigInteger.toCompactU8a(): ByteArray {
    if (compareTo(2.0.pow(30.0).minus(1).toLong().toBigInteger()) < 0) {
        return toLong().toCompactU8a()
    }
    val arr = toByteArray().reversedArray().trimTrailingZeros()

    return ByteBuffer.allocate(1 + arr.size)
        .order(ByteOrder.LITTLE_ENDIAN)
        .put((((arr.size - 4) shl 2) + CompactMode.Big.value).toByte())
        .put(arr)
        .array()
}
