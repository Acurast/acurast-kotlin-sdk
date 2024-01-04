package acurast.codec.extensions

import java.nio.ByteBuffer
import java.nio.ByteOrder

public fun Byte.toU8a(): ByteArray = ByteBuffer.allocate(1)
    .order(ByteOrder.LITTLE_ENDIAN)
    .put(this)
    .array()

public fun Byte.toCompactU8a(): ByteArray = (this.toInt() and 0xff).toLong().toCompactU8a()