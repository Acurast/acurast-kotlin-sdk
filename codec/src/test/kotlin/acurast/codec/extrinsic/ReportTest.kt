package acurast.codec.extrinsic

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.AccountId32
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.marketplace.ExecutionResult
import org.junit.Assert
import org.junit.Test

class ReportTest {
    @Test
    fun encodeCall() {
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