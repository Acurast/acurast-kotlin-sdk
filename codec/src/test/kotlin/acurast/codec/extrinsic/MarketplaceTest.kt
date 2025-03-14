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
        val secp256r1Pk = PublicKey.Secp256r1("0x032221f88ab3843cdf8ee8f0a410237b712278b99f9982504644a0018d94c179c5".hexToBa())
        val call = AcknowledgeMatchCall(callIndex, jobId, listOf(secp256r1Pk))
        Assert.assertEquals("2b03008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a4801000000000000000000000000000000040084032221f88ab3843cdf8ee8f0a410237b712278b99f9982504644a0018d94c179c5", call.toU8a().toHex())
    }

    @Test
    fun encodeAdvertiseCall() {
        val advertisement = MarketplaceAdvertisement(
            pricing = MarketplacePricing(
                feePerMillisecond = UInt128(BigInteger.ONE),
                feePerStorageByte = UInt128(BigInteger.ONE),
                baseFeePerExecution = UInt128(BigInteger.ONE),
                schedulingWindow = SchedulingWindow(SchedulingWindow.Kind.End, UInt64(10000))
            ),
            restriction = MarketplaceAdvertisementRestriction(
                maxMemory = 1,
                networkRequestQuota = 2,
                storageCapacity = 3,
                allowedConsumers = Option.some(listOf(MultiOrigin.Acurast(AccountId32("d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())))),
                availableModules = listOf(JobModule.DataEncryption),
            ),
        )
        val call = AdvertiseCall(byteArrayOf(0x2b, 0x00), advertisement)
        val expected = "2b00010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000001027000000000000010000000203000000010400d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d0400"
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
