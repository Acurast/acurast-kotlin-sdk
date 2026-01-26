package acurast.codec.type.tokenconversion

import acurast.codec.extensions.hexToBa
import org.junit.Test
import java.math.BigInteger
import java.nio.ByteBuffer
import kotlin.test.assertEquals

class TokenConversionTest {
    @Test
    fun decodeTokenConversion() {
        val encoded = "0x987367e90aa60400000000000000000098451200".hexToBa()
        val tokenConversion = TokenConversion.read(ByteBuffer.wrap(encoded))

        assertEquals(
            TokenConversion(
                amount = BigInteger("1308465702597528"),
                lockStart = 1_197_464U,
            ),
            tokenConversion,
        )
    }
}