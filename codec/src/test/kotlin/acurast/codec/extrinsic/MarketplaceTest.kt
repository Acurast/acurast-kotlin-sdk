package acurast.codec.extrinsic

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.*
import acurast.codec.type.acurast.*
import acurast.codec.type.marketplace.ExecutionResult
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class MarketplaceTest {
    @Test
    fun encodeAcknowledgeMatchCall() {
        val callIndex = byteArrayOf(0x2b, 0x03);
        val jobId = JobIdentifier(
            MultiOrigin.Acurast(AccountId32("8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48".hexToBa())),
            BigInteger.ONE
        )
        val call = AcknowledgeMatchCall(callIndex, jobId)
        Assert.assertEquals("2b03008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a4801000000000000000000000000000000", call.toU8a().toHex())
    }

    @Test
    fun encodeAdvertiseCall() {
        val rewardAsset = AssetId(
            MultiLocation(
                parents = 1,
                interior = JunctionsV1(
                    kind = JunctionsV1.Kind.X3,
                    junctions = listOf(
                        JunctionV1(JunctionV1.Kind.Parachain).setParachain(1000),
                        JunctionV1(JunctionV1.Kind.PalletInstance).setPalletInstance(50),
                        JunctionV1(JunctionV1.Kind.GeneralIndex).setGeneralIndex(BigInteger.valueOf(22)),
                    )
                )
            )
        )
        val advertisement = MarketplaceAdvertisement(
            pricing = listOf(
                MarketplacePricing(
                    rewardAsset,
                    feePerMillisecond = UInt128(BigInteger.ONE),
                    feePerStorageByte = UInt128(BigInteger.ONE),
                    baseFeePerExecution = UInt128(BigInteger.ONE),
                    schedulingWindow = SchedulingWindow(SchedulingWindow.Kind.End, UInt64(10000))
                )
            ),
            maxMemory = 1,
            networkRequestQuota = 2,
            storageCapacity = 3,
            allowedConsumers = Option.some(listOf(MultiOrigin.Acurast(AccountId32("d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())))),
            availableModules = listOf(JobModule.DataEncryption)
        )
        val call = AdvertiseCall(byteArrayOf(0x2b, 0x00), advertisement)
        val expected = "2b000400010300a10f04320558010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000001027000000000000010000000203000000010400d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d0400"
        Assert.assertEquals(expected, call.toU8a().toHex())
    }

    @Test
    fun encodeReportCall() {
        val callIndex = byteArrayOf(0x2b, 0x04)
        val jobId = JobIdentifier(
            MultiOrigin.Acurast(AccountId32("8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48".hexToBa())),
            BigInteger.ONE
        )
        val executionResult = ExecutionResult.success("""{ "some_field": 1 }""".encodeToByteArray())
        val call = ReportCall(callIndex, jobId, executionResult)
        val expected = "2b04008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a4801000000000000000000000000000000004c7b2022736f6d655f6669656c64223a2031207d"
        Assert.assertEquals(expected, call.toU8a().toHex())
    }
}
