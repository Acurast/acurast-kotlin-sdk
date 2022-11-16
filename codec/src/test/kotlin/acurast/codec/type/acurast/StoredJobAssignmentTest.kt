package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.AccountIdentifier
import acurast.codec.type.MultiAddress
import org.junit.Assert
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.charset.Charset

class Test {
    @Test
    fun decodeStoredJobAssignment() {
        val assignment = listOf("0x1aee6710ac79060b1e13291ba85112af98a4bc93baf483e5448a2f435589310d11b66cf9180ffd003cdccec251febbe9162d2da75808479e427fd639d773aac9d2249df93828ead8ed33e38e0f65e1365bf26ec45ab90e8f1b701097ccad556dd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27dd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432","0x00")
        val jobAssignment = StoredJobAssignment.read(assignment)

        Assert.assertEquals(jobAssignment.script.toHex(), "697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432")
        Assert.assertEquals(jobAssignment.processor.type, AccountIdentifier.AccountID)
        Assert.assertEquals(jobAssignment.processor.bytes.toHex(), "162d2da75808479e427fd639d773aac9d2249df93828ead8ed33e38e0f65e136")
        Assert.assertEquals(jobAssignment.requester.type, AccountIdentifier.AccountID)
        Assert.assertEquals(jobAssignment.requester.bytes.toHex(), "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
        Assert.assertEquals(jobAssignment.slot, 0)
    }
}
