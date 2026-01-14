package acurast.codec.type.compute

import acurast.codec.extensions.readU128
import acurast.codec.extensions.readU256
import java.math.BigInteger
import java.nio.ByteBuffer

public data class Delegation(
    val stake: Stake,
    val rewardWeight: BigInteger,
    val slashWeight: BigInteger,
    val rewardDebt: BigInteger,
    val slashDebt: BigInteger,
) {
    public companion object {
        public fun read(bytes: ByteBuffer): Delegation {
            val stake = Stake.read(bytes)
            val rewardWeight = bytes.readU256()
            val slashWeight = bytes.readU256()
            val rewardDebt = bytes.readU128()
            val slashDebt = bytes.readU128()

            return Delegation(stake, rewardWeight, slashWeight, rewardDebt, slashDebt)
        }
    }
}
