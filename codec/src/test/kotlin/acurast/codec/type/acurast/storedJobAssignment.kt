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
        val jobAssignment = ByteBuffer.wrap("0x04d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27dd4697066733a2f2f516d54343251747a69524b79484d566a68623932746b4d7a6e4d38674c4455454d4e50764b53576268706654686d".hexToBa())
            .readList {
                readStoredJobAssignment()
            }

        Assert.assertEquals(jobAssignment.size, 1)
        Assert.assertEquals(jobAssignment[0].script.toHex(), "697066733a2f2f516d54343251747a69524b79484d566a68623932746b4d7a6e4d38674c4455454d4e50764b53576268706654686d")
        Assert.assertEquals(jobAssignment[0].accountId.type, AccountIdentifier.AccountID)
        Assert.assertEquals(jobAssignment[0].accountId.bytes.toHex(), "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
    }
}
