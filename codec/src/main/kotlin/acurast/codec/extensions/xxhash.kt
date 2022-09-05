package acurast.codec.extensions

import net.jpountz.xxhash.XXHashFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Generates 128-bit hashes using vectorized arithmetic.
 */
public fun ByteArray.xxH128(): ByteArray {
    val factory = XXHashFactory.fastestInstance()
    val buf = ByteBuffer.wrap(this)

    val ret = ByteBuffer.allocate(16)
    ret.order(ByteOrder.LITTLE_ENDIAN)
    ret.putLong(factory.hash64().hash(buf, 0))
    buf.rewind()
    ret.putLong(factory.hash64().hash(buf, 1))
    return ret.array()
}