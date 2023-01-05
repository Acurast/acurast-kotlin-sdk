package acurast.codec.extensions

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

public fun Long.toU8a(): ByteArray = ByteBuffer.allocate(8)
    .order(ByteOrder.LITTLE_ENDIAN)
    .putLong(this)
    .array()

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