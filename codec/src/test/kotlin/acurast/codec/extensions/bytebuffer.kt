package acurast.codec.extensions

import org.junit.Assert
import org.junit.Test
import java.nio.ByteBuffer

class ByteBufferTest {
    @Test
    fun readU128() {
        val buffer = ByteBuffer.wrap("02000000000000000000000000000000".hexToBa())
        val expected = 2
        Assert.assertEquals(
            expected,
            buffer.readU128().toInt(),
        )
    }

    @Test
    fun readCompact_u8() {
        val original = 244
        val value = ByteBuffer.wrap("d103".hexToBa())
        val decoded = value.readCompactInteger()
        Assert.assertEquals(
            original,
            decoded.toInt(),
        )
    }

    @Test
    fun readCompact_u16() {
        val original = 2244
        val value = ByteBuffer.wrap("1123".hexToBa())
        val decoded = value.readCompactInteger()
        Assert.assertEquals(
            original,
            decoded.toInt(),
        )
    }

    @Test
    fun readCompact_u32() {
        val original = 22244
        val value = ByteBuffer.wrap("925b0100".hexToBa())
        val decoded = value.readCompactInteger()
        Assert.assertEquals(
            original,
            decoded.toInt(),
        )
    }

    @Test
    fun readCompact_u64() {
        val original = 2545544
        val value = ByteBuffer.wrap("225e9b00".hexToBa())
        val decoded = value.readCompactInteger()
        Assert.assertEquals(
            original,
            decoded.toInt(),
        )
    }

    @Test
    fun readCompact_u128() {
        val original = 16383
        val value = ByteBuffer.wrap("fdff".hexToBa())
        val decoded = value.readCompactInteger()
        Assert.assertEquals(
            original,
            decoded.toInt(),
        )
    }
}