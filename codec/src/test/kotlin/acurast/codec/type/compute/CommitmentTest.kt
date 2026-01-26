package acurast.codec.type.compute

import acurast.codec.extensions.hexToBa
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import kotlin.test.assertEquals

class CommitmentTest {
    @Test
    fun decodeCommitment() {
        val encoded1 = "0x010080145e6e2f2e0000000000000000000080145e6e2f2e000000000000000000a9a9860080700000007a7fa639b1520000000000000000000000000000000000000000000000000000006e58f8ca880a0000000000000000000000000000000000000000000000000000000000002680c85e0ab789010000000000000000260023f8d450c600000000000000000001c12600000080145e6e2f2e000000000000000000000000000000000000000000000000000080145e6e2f2e0000000000000000000000000000000000000000000000000026705f1b8108c50000000000000000000000000000000000000000000000000026f00482b66e8801000000000000000000000000000000000000000000000000c22600000080145e6e2f2e000000000000000000000000000000000000000000000000000080145e6e2f2e00000000000000000000000000000000000000000000000000260023f8d450c6000000000000000000000000000000000000000000000000002680c85e0ab78901000000000000000000000000000000000000000000000000016c2d7a00d3a9de1531849c4fcd83e68e000000000000000000000000000000000000000094b31d768524024f4116459c0000000000000000000000000000000000000000a9a98600db10eb27ba847d93b170380f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c426000042260000".hexToBa()
        val commitment1 = Commitment.read(ByteBuffer.wrap(encoded1))

        assertEquals(
            Commitment(
                stake = Stake(
                    amount = BigInteger("13000000000000000"),
                    rewardableAmount = BigInteger("13000000000000000"),
                    created = 8_825_257U,
                    cooldownPeriod = 28_800U,
                    cooldownStarted = null,
                    accruedReward = BigInteger("90921129901946"),
                    accruedSlash = BigInteger("0"),
                    allowAutoCompound = false,
                    paid = BigInteger("11582637103214"),
                    appliedSlash = BigInteger("0"),
                ),
                commission = BigDecimal.valueOf(0, 9), // 0.0
                delegationsTotalAmount = BigInteger("110820921015042086"),
                delegationsTotalRewardableAmount = BigInteger("55820921015042086"),
                weights = MemoryBuffer(
                    past = Pair(9_921U, CommitmentWeights(
                        selfRewardWeight = BigInteger("13000000000000000"),
                        selfSlashWeight = BigInteger("13000000000000000"),
                        delegationsRewardWeight = BigInteger("55459921015042086"),
                        delegationsSlashWeight = BigInteger("110459921015042086"),
                    )),
                    current = Pair(9_922U, CommitmentWeights(
                        selfRewardWeight = BigInteger("13000000000000000"),
                        selfSlashWeight = BigInteger("13000000000000000"),
                        delegationsRewardWeight = BigInteger("55820921015042086"),
                        delegationsSlashWeight = BigInteger("110820921015042086"),
                    )),
                ),
                poolRewards = MemoryBuffer(
                    past = Pair(8_007_020U, PoolReward(
                        rewardPerWeight = BigInteger("44225546750470760874800753107"),
                        slashPerWeight = BigInteger("48363182510477381092016763796"),
                    )),
                    current = Pair(8_825_257U, PoolReward(
                        rewardPerWeight = BigInteger("4710507173966174342242570459"),
                        slashPerWeight = BigInteger("0"),
                    )),
                ),
                lastScoringEpoch = 9_924U,
                lastSlashingEpoch = 9_794U,
            ),
            commitment1,
        )

        val encoded2 = "0x010b25cabf076a550000000000000000000b25cabf076a55000000000000000000398182008070000000f99b15409f0c00000000000000000000000000000000000000000000000000000178ded3a876a4040000000000000000000000000000000000000000000000000080f0fa02c7e8a6e36f81d20000000000000000007fa0619b29eccf00000000000000000001bd2600000b4505d56a0550000000000000000000000000000000000000000000000000000b4505d56a055000000000000000000000000000000000000000000000000000071133cdd558b0000000000000000000000000000000000000000000000000009034c3fe17ceb100000000000000000000000000000000000000000000000000c22600000b25cabf076a55000000000000000000000000000000000000000000000000000b25cabf076a5500000000000000000000000000000000000000000000000000954dce1ef476b0000000000000000000000000000000000000000000000000001e715e5036ecb100000000000000000000000000000000000000000000000000011ac078002c01340325db7449f6fbdb410400000000000000000000000000000000000000ef503f81ae3fe501e3c75402000000000000000000000000000000000000000039818200ccf3fcecbf8cc71a901b55bd00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c526000002250000".hexToBa()
        val commitment2 = Commitment.read(ByteBuffer.wrap(encoded2))

        assertEquals(
            Commitment(
                stake = Stake(
                    amount = BigInteger("24041954535417099"),
                    rewardableAmount = BigInteger("24041954535417099"),
                    created = 8_552_761U,
                    cooldownPeriod = 28_800U,
                    cooldownStarted = null,
                    accruedReward = BigInteger("13878114491385"),
                    accruedSlash = BigInteger("0"),
                    allowAutoCompound = true,
                    paid = BigInteger("1306729452396152"),
                    appliedSlash = BigInteger("0"),
                ),
                commission = BigDecimal.valueOf(50000000, 9), // 0.05
                delegationsTotalAmount = BigInteger("59252062669957319"),
                delegationsTotalRewardableAmount = BigInteger("58524983623786623"),
                weights = MemoryBuffer(
                    past = Pair(9_917U, CommitmentWeights(
                        selfRewardWeight = BigInteger("22523954535417099"),
                        selfSlashWeight = BigInteger("22523954535417099"),
                        delegationsRewardWeight = BigInteger("49637271195029767"),
                        delegationsSlashWeight = BigInteger("50047673331561616"),
                    )),
                    current = Pair(9_922U, CommitmentWeights(
                        selfRewardWeight = BigInteger("24041954535417099"),
                        selfSlashWeight = BigInteger("24041954535417099"),
                        delegationsRewardWeight = BigInteger("49670386762010005"),
                        delegationsSlashWeight = BigInteger("50080788898541854"),
                    )),
                ),
                poolRewards = MemoryBuffer(
                    past = Pair(7_913_498U, PoolReward(
                        rewardPerWeight = BigInteger("337295120307119722458126745900"),
                        slashPerWeight = BigInteger("721463726967900162020561135"),
                    )),
                    current = Pair(8_552_761U, PoolReward(
                        rewardPerWeight = BigInteger("58595555713057311284724036556"),
                        slashPerWeight = BigInteger("0"),
                    )),
                ),
                lastScoringEpoch = 9_925U,
                lastSlashingEpoch = 9_474U,
            ),
            commitment2,
        )
    }
}
