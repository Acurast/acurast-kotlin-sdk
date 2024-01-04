package acurast.codec.type

import acurast.codec.extensions.littleEndian
import acurast.codec.extensions.readCompactInteger
import java.nio.ByteBuffer

public data class Weight(
    /// The weight of computational time used based on some reference hardware.
    val refTime: Long,
    /// The weight of storage space used by proof of validity.
    val proofSize: Long,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): Weight {
            val refTime = buffer.readCompactInteger().toLong()
            val proofSize = buffer.readCompactInteger().toLong()
            return Weight(
                refTime,
                proofSize,
            )
        }
    }
}

public fun ByteBuffer.readWeight(): Weight = littleEndian { Weight.read(this) }
