package acurast.codec.extensions

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

public fun BigInteger.toCompactU8a(): ByteArray {
    if (compareTo(2.0.pow(30.0).minus(1).toLong().toBigInteger()) < 0) {
        return toLong().toCompactU8a()
    }
    val arr = toByteArray().reversedArray().trimTrailingZeros()

    return ByteBuffer.allocate(1 + arr.size)
        .order(ByteOrder.LITTLE_ENDIAN)
        .put((((arr.size - 4) shl 2) + 0x03).toByte())
        .put(arr)
        .array()
}
