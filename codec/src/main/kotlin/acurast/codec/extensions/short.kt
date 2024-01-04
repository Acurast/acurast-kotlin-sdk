package acurast.codec.extensions

import java.nio.ByteBuffer
import java.nio.ByteOrder

public fun Short.toU8a(): ByteArray = ByteBuffer.allocate(2)
    .order(ByteOrder.LITTLE_ENDIAN)
    .putShort(this)
    .array()