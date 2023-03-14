package acurast.codec.type.acurast

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.AccountId32
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.nio.ByteBuffer

class JobIdentifierTest {
    @Test
    fun encodeJobIdentifier() {
        val jobId = JobIdentifier(
            MultiOrigin.Acurast(AccountId32("1cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c".hexToBa())),
            BigInteger.ONE
        )

        val encoded = jobId.toU8a()
        val decoded = JobIdentifier.read(ByteBuffer.wrap(encoded))

        Assert.assertEquals(decoded.origin.kind, jobId.origin.kind)
        Assert.assertEquals(decoded.origin.source.toHex(), jobId.origin.source.toHex())
        Assert.assertEquals(decoded.id, jobId.id)
    }
}
