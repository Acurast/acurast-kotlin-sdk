package acurast.codec

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

// Long

public fun Long.toCompactU8a(): ByteArray {
    val x = this
    val ret: ByteBuffer
    when (x) {
        in 0 until 64 -> {
            ret = ByteBuffer.allocate(1)
            ret.order(ByteOrder.LITTLE_ENDIAN)
            ret.put(((x shl 2) + 0x00).toByte())
        }
        in 64 until 2.0.pow(14.0).minus(1).toLong() -> {
            ret = ByteBuffer.allocate(2)
            ret.order(ByteOrder.LITTLE_ENDIAN)
            ret.putShort(((x shl 2) + 0x01).toShort())
        }
        in 64 until 2.0.pow(30.0).minus(1).toLong() -> {
            ret = ByteBuffer.allocate(4)
            ret.order(ByteOrder.LITTLE_ENDIAN)
            ret.putInt(((x shl 2) + 0x02).toInt())
        }
        else -> {
            return this.toBigInteger().toCompactU8a()
        }
    }
    return ret.array()
}

public fun Int.toU8a(): ByteArray = ByteBuffer.allocate(4)
    .order(ByteOrder.LITTLE_ENDIAN)
    .putInt(this)
    .array()

// BigInteger

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

// Byte

public fun Byte.toU8a(): ByteArray = ByteBuffer.allocate(1)
        .order(ByteOrder.LITTLE_ENDIAN)
        .put(this)
        .array()

// ByteArray

public fun ByteArray.toU8a(): ByteArray {
    return this.size.toLong().toCompactU8a() + this
}

public fun ByteArray.trimTrailingZeros() : ByteArray = this.dropLastWhile { it == 0x00.toByte() }.toByteArray()