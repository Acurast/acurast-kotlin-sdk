package acurast.codec.extrinsic

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.AccountId32
import acurast.codec.type.acurast.JobIdentifier
import org.junit.Assert
import org.junit.Test

class AcknowledgeMatchTest {
    @Test
    fun encodeCall() {
        val callIndex = byteArrayOf(0x2b, 0x03);
        val script = "697066733a2f2f516d5378377a44706b76627975674c33553339467454617357784d6d6b6647363773783977614752564837415145".hexToBa();
        val jobId = JobIdentifier(
            requester = AccountId32("8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48".hexToBa()),
            script = script,
        )
        val call = AcknowledgeMatchCall(callIndex, jobId)
        Assert.assertEquals(call.toU8a().toHex(), "2b038eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48d4697066733a2f2f516d5378377a44706b76627975674c33553339467454617357784d6d6b6647363773783977614752564837415145")
    }

}