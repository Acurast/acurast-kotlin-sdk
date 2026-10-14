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

    @Test
    fun readU128_highBitIsUnsigned() {
        val buffer = ByteBuffer.wrap("ffffffffffffffffffffffffffffffff".hexToBa())
        Assert.assertEquals(java.math.BigInteger.TWO.pow(128) - java.math.BigInteger.ONE, buffer.readU128())
    }

    @Test
    fun readCompact_fourByteUpperBound() {
        val value = ByteBuffer.wrap("feffffff".hexToBa())
        Assert.assertEquals(0x3fffffff.toBigInteger(), value.readCompactInteger())
    }

    @Test
    fun readCompact_bigIntegerHighBitIsUnsigned() {
        val value = ByteBuffer.wrap("13ffffffffffffffff".hexToBa())
        Assert.assertEquals(java.math.BigInteger.TWO.pow(64) - java.math.BigInteger.ONE, value.readCompactInteger())
    }

    @Test
    fun readCompact_roundTripsEncoding() {
        listOf(0L, 63L, 64L, 16_383L, 16_384L, 0x3fffffffL, 0x40000000L, Long.MAX_VALUE).forEach {
            val value = it.toBigInteger()
            Assert.assertEquals(value, ByteBuffer.wrap(value.toCompactU8a()).readCompactInteger())
        }
    }

    @Test
    fun readString_decodesUtf8() {
        val buffer = ByteBuffer.wrap(byteArrayOf(0x0c) + "\u00fcb".toByteArray(Charsets.UTF_8))
        Assert.assertEquals("\u00fcb", buffer.readString())
    }

    @Test(expected = ScaleDecoderException::class)
    fun readBoolean_rejectsInvalidValues() {
        ByteBuffer.wrap(byteArrayOf(2)).readBoolean()
    }

    @Test(expected = ScaleDecoderException::class)
    fun readByteArray_rejectsLengthsBeyondTheData() {
        ByteBuffer.wrap("1000".hexToBa()).readByteArray()
    }
}
