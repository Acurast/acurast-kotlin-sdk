package acurast.codec.extrinsic

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.*
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.MarketplaceAdvertisement
import acurast.codec.type.acurast.MarketplacePricing
import acurast.codec.type.acurast.SchedulingWindow
import acurast.codec.type.marketplace.ExecutionResult
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class MarketplaceTest {
    @Test
    fun encodeAcknowledgeMatchCall() {
        val callIndex = byteArrayOf(0x2b, 0x03);
        val script = "697066733a2f2f516d5378377a44706b76627975674c33553339467454617357784d6d6b6647363773783977614752564837415145".hexToBa();
        val jobId = JobIdentifier(
            requester = AccountId32("8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48".hexToBa()),
            script = script,
        )
        val call = AcknowledgeMatchCall(callIndex, jobId)
        Assert.assertEquals(call.toU8a().toHex(), "2b038eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48d4697066733a2f2f516d5378377a44706b76627975674c33553339467454617357784d6d6b6647363773783977614752564837415145")
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
            allowedConsumers = Option.some(listOf(AccountId32("d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())))
        )
        val call = AdvertiseCall(byteArrayOf(0x2b, 0x00), advertisement)
        val expected = "2b000400010300a10f043205580100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000010270000000000000100000002030000000104d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
        Assert.assertEquals(expected, call.toU8a().toHex())
    }

    @Test
    fun encodeReportCall() {
        val callIndex = byteArrayOf(0x2b, 0x04);
        val script = "697066733a2f2f516d5378377a44706b76627975674c33553339467454617357784d6d6b6647363773783977614752564837415145".hexToBa();
        val jobId = JobIdentifier(
            requester = AccountId32("8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48".hexToBa()),
            script = script,
        )
        val executionResult = ExecutionResult(ExecutionResult.Kind.Success, """{ "some_field": 1 }""".encodeToByteArray())
        val call = ReportCall(callIndex, jobId, false, executionResult)
        val expected = "2b048eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48d4697066733a2f2f516d5378377a44706b76627975674c33553339467454617357784d6d6b664736377378397761475256483741514500004c7b2022736f6d655f6669656c64223a2031207d"
        Assert.assertEquals(expected, call.toU8a().toHex())
    }
}