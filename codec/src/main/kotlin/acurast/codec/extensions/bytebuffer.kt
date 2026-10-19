package acurast.codec.extensions

import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.absoluteValue

public class ScaleDecoderException(msg: String?) : Exception(msg)

public fun ByteBuffer.positionRelative(offset: Int): ByteBuffer {
    val startingPosition = if (offset >= 0) position() else remaining()
    return position(startingPosition + offset)
}

public inline fun <reified T> ByteBuffer.littleEndian(decoder: ByteBuffer.() -> T): T {
    order(ByteOrder.LITTLE_ENDIAN)
    return decoder()
}

public fun ByteBuffer.readU8(): UByte = littleEndian { get().toUByte() }
public fun ByteBuffer.readU16(): UShort = littleEndian { short.toUShort() }
public fun ByteBuffer.readU32(): UInt = littleEndian { int.toUInt() }
public fun ByteBuffer.readU64(): ULong = littleEndian { long.toULong() }
public fun ByteBuffer.readU128(): BigInteger = readUnsigned(16)
public fun ByteBuffer.readU256(): BigInteger = readUnsigned(32)

/** Reads a little-endian unsigned integer of [byteCount] bytes. */
public fun ByteBuffer.readUnsigned(byteCount: Int): BigInteger = BigInteger(1, readByteArray(byteCount).reversedArray())

/** Reads a little-endian two's complement signed integer of [byteCount] bytes. */
public fun ByteBuffer.readSigned(byteCount: Int): BigInteger = BigInteger(readByteArray(byteCount).reversedArray())

public fun ByteBuffer.readByte(): Byte = littleEndian {
    get()
}

public fun ByteBuffer.readString(): String = littleEndian {
    readByteArray(readCompactInt()).toString(Charsets.UTF_8)
}

public fun ByteBuffer.readBoolean(): Boolean = littleEndian {
    when (val value = this.get().toInt()) {
        0 -> false
        1 -> true
        else -> throw ScaleDecoderException("Invalid boolean value $value")
    }
}

public fun <T> ByteBuffer.readList(elementParser: ByteBuffer.() -> T): List<T> = littleEndian {
    val size = readCompactInt()
    // The size is untrusted input: don't preallocate more than the remaining data could hold.
    val list = ArrayList<T>(minOf(size, remaining()))
    for (i in 0 until size) {
        list.add(this.elementParser())
    }
    list
}

public fun ByteBuffer.readCompactInteger(): BigInteger = littleEndian {
    val compactByte = this.get().toInt() and 0xff

    return when (compactByte and 0b11) {
        0b00 -> BigInteger.valueOf((compactByte shr 2).toLong())
        0b01 -> BigInteger.valueOf(((compactByte or ((get().toInt() and 0xff) shl 8)) shr 2).toLong())
        0b10 -> BigInteger.valueOf((compactByte.toLong() or (readUnsigned(3).toLong() shl 8)) shr 2)
        else -> readUnsigned((compactByte shr 2) + 4)
    }
}

/** Reads a compact-encoded length or index, which must fit an [Int]. */
public fun ByteBuffer.readCompactInt(): Int {
    val value = readCompactInteger()
    if (value.bitLength() > 31) throw ScaleDecoderException("Compact value $value out of Int range")
    return value.toInt()
}

public fun ByteBuffer.readByteArray(n: Int? = null): ByteArray = littleEndian {
    val size = n ?: readCompactInt()
    if (size < 0 || size > remaining()) throw ScaleDecoderException("Cannot read $size bytes, ${remaining()} remaining")
    ByteArray(size).apply { get(this) }
}

public fun ByteBuffer.readBytes(offset: Int): ByteArray = littleEndian {
    ByteArray(offset).apply { get(this) }
}

public fun ByteBuffer.readOptionalBoolean(): Boolean? = littleEndian {
    when (this.get() == 0x00.toByte()) {
        true -> null
        else -> this.get() == 0x02.toByte()
    }
}

public inline fun <reified T> ByteBuffer.readOptional(optionalParser: ByteBuffer.() -> T): T? = littleEndian {
    when (T::class) {
        Boolean::class -> readOptionalBoolean() as T?
        else -> {
            when (readBoolean()) {
                true -> this.optionalParser()
                else -> null
            }
        }
    }
}

public fun ByteBuffer.readPerbill(): BigDecimal =
    BigDecimal(readU32().toString()).times(BigDecimal.valueOf(1, 9))

public fun ByteBuffer.readPerquintill(): BigDecimal =
    BigDecimal(readU64().toString()).times(BigDecimal.valueOf(1, 18))

public fun ByteBuffer.readFixedU128(): BigDecimal =
    readU128().toBigDecimal().times(BigDecimal.valueOf(1, 18))