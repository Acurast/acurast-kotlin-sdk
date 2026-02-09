package acurast.codec.type.compute

import acurast.codec.extensions.readOptional
import acurast.codec.extensions.readPerbill
import acurast.codec.extensions.readU128
import acurast.codec.extensions.readU256
import acurast.codec.extensions.readU32
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer

public data class Commitment(
    val stake: Stake?,
    val commission: BigDecimal,
    val delegationsTotalAmount: BigInteger,
    val delegationsTotalRewardableAmount: BigInteger,
    val weights: MemoryBuffer<UInt, CommitmentWeights>,
    val poolRewards: MemoryBuffer<UInt, PoolReward>,
    val lastScoringEpoch: UInt,
    val lastSlashingEpoch: UInt,
) {
    public companion object {
        public fun read(bytes: ByteBuffer): Commitment {
            val stake = bytes.readOptional { Stake.read(this) }
            val commission = bytes.readPerbill()
            val delegationsTotalAmount = bytes.readU128()
            val delegationsTotalRewardableAmount = bytes.readU128()
            val weights = MemoryBuffer.readWithU32Timestamp(bytes) { CommitmentWeights.read(it) }
            val poolRewards = MemoryBuffer.readWithU32Timestamp(bytes) { PoolReward.read(it) }
            val lastScoringEpoch = bytes.readU32()
            val lastSlashingEpoch = bytes.readU32()

            return Commitment(
                stake,
                commission,
                delegationsTotalAmount,
                delegationsTotalRewardableAmount,
                weights,
                poolRewards,
                lastScoringEpoch,
                lastSlashingEpoch,
            )
        }
    }
}

public data class CommitmentWeights(
    val selfRewardWeight: BigInteger,
    val selfSlashWeight: BigInteger,
    val delegationsRewardWeight: BigInteger,
    val delegationsSlashWeight: BigInteger,
) {
    public companion object {
        public fun read(bytes: ByteBuffer): CommitmentWeights {
            val selfRewardWeight = bytes.readU256()
            val selfSlashWeight = bytes.readU256()
            val delegationsRewardWeight = bytes.readU256()
            val delegationsSlashWeight = bytes.readU256()

            return CommitmentWeights(selfRewardWeight, selfSlashWeight, delegationsRewardWeight, delegationsSlashWeight)
        }
    }
}

public data class PoolReward(
    val rewardPerWeight: BigInteger,
    val slashPerWeight: BigInteger,
) {
    public companion object {
        public fun read(bytes: ByteBuffer): PoolReward {
            val rewardPerWeight = bytes.readU256()
            val slashPerWeight = bytes.readU256()

            return PoolReward(rewardPerWeight, slashPerWeight)
        }
    }
}

public data class MemoryBuffer<T, S>(
    val past: Pair<T, S>?,
    val current: Pair<T, S>,
) {
    public companion object {
        public fun <T, S> read(bytes: ByteBuffer, readTimestamp: (ByteBuffer) -> T, readStruct: (ByteBuffer) -> S): MemoryBuffer<T, S> {
            val past = bytes.readOptional { Pair(readTimestamp(this), readStruct(this)) }
            val current = Pair(readTimestamp(bytes), readStruct(bytes))

            return MemoryBuffer(past, current)
        }

        public fun <S> readWithU32Timestamp(bytes: ByteBuffer, readStruct: (ByteBuffer) -> S): MemoryBuffer<UInt, S> =
            read(bytes, readTimestamp = { it.readU32() }, readStruct = readStruct)
    }
}