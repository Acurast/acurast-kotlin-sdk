package acurast.codec.type.compute

import acurast.codec.extensions.hexToBa
import java.math.BigInteger
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

class DelegationTest {
    @Test
    fun decodeDelegation() {
        val encoded1 = "0x0090c3dc5348010000000000000000000090c3dc534801000000000000000000df438800807000000000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000090c3dc534801000000000000000000000000000000000000000000000000000090c3dc53480100000000000000000000000000000000000000000000000000dd2c1e7c6e010000000000000000000000000000000000000000000000000000".hexToBa()
        val delegation1 = Delegation.read(ByteBuffer.wrap(encoded1))

        assertEquals(
            Delegation(
                stake = Stake(
                    amount = BigInteger("361000000000000"),
                    rewardableAmount = BigInteger("361000000000000"),
                    created = 8_930_271U,
                    cooldownPeriod = 28_800U,
                    cooldownStarted = null,
                    accruedReward = BigInteger("0"),
                    accruedSlash = BigInteger("0"),
                    allowAutoCompound = true,
                    paid = BigInteger("0"),
                    appliedSlash = BigInteger("0"),
                ),
                rewardWeight = BigInteger("361000000000000"),
                slashWeight = BigInteger("361000000000000"),
                rewardDebt = BigInteger("1574040382685"),
                slashDebt = BigInteger("0"),
            ),
            delegation1,
        )
    }
}