package acurast.codec.type

import acurast.codec.extensions.toHex
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class FixedLengthIntegerTest {
    @Test
    fun encodeU8() {
        val value = UInt8(244.toByte())
        val encoded = value.toU8a().toHex()
        Assert.assertEquals(
            "f4",
            encoded,
        )
    }

    @Test
    fun encodeCompact_u8() {
        val value = UInt8(244.toByte())
        val encoded = value.toCompactU8a().toHex()
        Assert.assertEquals(
            "d103",
            encoded,
        )
    }

    @Test
    fun encodeU16() {
        val value = UInt16(2244)
        val encoded = value.toU8a().toHex()
        Assert.assertEquals(
            "c408",
            encoded,
        )
    }

    @Test
    fun encodeCompact_u16() {
        val value = UInt16(2244)
        val encoded = value.toCompactU8a().toHex()
        Assert.assertEquals(
            "1123",
            encoded,
        )
    }

    @Test
    fun encodeU32() {
        val value = UInt32(22244)
        val encoded = value.toU8a().toHex()
        Assert.assertEquals(
            "e4560000",
            encoded,
        )
    }

    @Test
    fun encodeCompact_u32() {
        val value = UInt32(22244)
        val encoded = value.toCompactU8a().toHex()
        Assert.assertEquals(
            "925b0100",
            encoded,
        )
    }

    @Test
    fun encodeU64() {
        val value = UInt64(2545544)
        val encoded = value.toU8a().toHex()
        Assert.assertEquals(
            "88d7260000000000",
            encoded,
        )
    }

    @Test
    fun encodeCompact_u64() {
        val value = UInt64(2545544)
        val encoded = value.toCompactU8a().toHex()
        Assert.assertEquals(
            "225e9b00",
            encoded,
        )
    }

    @Test
    fun encodeU128() {
        val value = UInt128(BigInteger.valueOf(2))
        val encoded = value.toU8a().toHex()
        Assert.assertEquals(
            "02000000000000000000000000000000",
            encoded,
        )
    }

    @Test
    fun encodeCompact_u128() {
        val value = UInt128(BigInteger.valueOf(16383))
        val encoded = value.toCompactU8a().toHex()
        Assert.assertEquals(
            "fdff",
            encoded,
        )
    }
}