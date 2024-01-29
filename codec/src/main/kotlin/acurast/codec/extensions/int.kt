package acurast.codec.extensions

import java.nio.ByteBuffer
import java.nio.ByteOrder

public fun Int.toU8a(): ByteArray = ByteBuffer.allocate(4)
    .order(ByteOrder.LITTLE_ENDIAN)
    .putInt(this)
    .array()

public fun UInt.toU8a(): ByteArray = toInt().toU8a()