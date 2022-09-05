package acurast.codec.extensions

import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(ExperimentalUnsignedTypes::class)
public fun ByteBuffer.readU32(): UInt = order(ByteOrder.LITTLE_ENDIAN).int.toUInt()