package acurast.codec.type.compute

import acurast.codec.extensions.readBoolean
import acurast.codec.extensions.readOptional
import acurast.codec.extensions.readU128
import acurast.codec.extensions.readU32
import java.math.BigInteger
import java.nio.ByteBuffer

public data class Stake(
    val amount: BigInteger,
    val rewardableAmount: BigInteger,
    val created: UInt,
    val cooldownPeriod: UInt,
    val cooldownStarted: UInt?,
    val accruedReward: BigInteger,
    val accruedSlash: BigInteger,
    val allowAutoCompound: Boolean,
    val paid: BigInteger,
    val appliedSlash: BigInteger,
) {
    public companion object {
        public fun read(bytes: ByteBuffer): Stake {
            val amount = bytes.readU128()
            val rewardableAmount = bytes.readU128()
            val created = bytes.readU32()
            val cooldownPeriod = bytes.readU32()
            val cooldownStarted = bytes.readOptional { readU32() }
            val accruedReward = bytes.readU128()
            val accruedSlash = bytes.readU128()
            val allowAutoCompound = bytes.readBoolean()
            val paid = bytes.readU128()
            val appliedSlash = bytes.readU128()

            return Stake(
                amount,
                rewardableAmount,
                created,
                cooldownPeriod,
                cooldownStarted,
                accruedReward,
                accruedSlash,
                allowAutoCompound,
                paid,
                appliedSlash,
            )
        }
    }
}