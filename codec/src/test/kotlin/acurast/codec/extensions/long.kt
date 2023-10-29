package acurast.codec.extensions

import acurast.codec.type.UInt128
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.nio.ByteBuffer

class LongTest {
    @Test
    fun toCompactU8a() {
        val value = UInt128(BigInteger.valueOf(16380L))
        val encoded = value.toCompactU8a().toHex()
        Assert.assertEquals(
            "f1ff",
            encoded,
        )
    }

    @Test
    fun toCompactU8a2() {
        val value = UInt128(BigInteger.valueOf(16383L))
        val encoded = value.toCompactU8a().toHex()
        Assert.assertEquals(
            "fdff",
            encoded,
        )
    }

    @Test
    fun fromCompactU8a() {
        val original = 16383
        val value = ByteBuffer.wrap("fdff".hexToBa())
        val decoded = value.readCompactInteger()
        Assert.assertEquals(
            decoded,
            original
        )
    }
}