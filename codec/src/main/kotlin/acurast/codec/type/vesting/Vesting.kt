package acurast.codec.type.vesting

import acurast.codec.extensions.readU128
import acurast.codec.extensions.readU32
import java.math.BigInteger
import java.nio.ByteBuffer

public data class Vesting(
    val locked: BigInteger,
    val perBlock: BigInteger,
    val startingBlock: UInt,
) {
    public companion object {
        public fun read(bytes: ByteBuffer): Vesting {
            val locked = bytes.readU128()
            val perBlock = bytes.readU128()
            val startingBlock = bytes.readU32()

            return Vesting(locked, perBlock, startingBlock)
        }
    }
}
