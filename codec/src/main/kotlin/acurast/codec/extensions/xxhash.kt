package acurast.codec.extensions

import net.jpountz.xxhash.XXHashFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder

public fun ByteArray.xxH128(): ByteArray = XXHash.H128.hash(this)
public fun ByteArray.xxH64(): ByteArray = XXHash.H64.hash(this)

private sealed class XXHash {
    object H64 : XXHash() {
        override val segments: Int = 1
    }

    object H128 : XXHash() {
        override val segments: Int = 2
    }

    protected abstract val segments: Int

    /**
     * Generates hashes using vectorized arithmetic.
     */
    fun hash(array: ByteArray): ByteArray {
        val factory = XXHashFactory.fastestInstance()
        val buf = ByteBuffer.wrap(array)
        val hash = ByteBuffer.allocate(8 * segments).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            repeat(segments) { i ->
                buf.rewind()
                putLong(factory.hash64().hash(buf, i.toLong()))
            }
        }

        return hash.array()
    }
}