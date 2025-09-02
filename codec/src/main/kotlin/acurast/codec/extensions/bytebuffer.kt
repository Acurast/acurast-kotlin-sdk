package acurast.codec.extensions

import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

public class ScaleDecoderException(msg: String?) : Exception(msg)

public inline fun <reified T> ByteBuffer.littleEndian(decoder: ByteBuffer.() -> T): T {
    order(ByteOrder.LITTLE_ENDIAN)
    return decoder()
}

public fun ByteBuffer.readU8(): UByte = littleEndian { get().toUByte() }
public fun ByteBuffer.readU16(): UShort = littleEndian { short.toUShort() }
public fun ByteBuffer.readU32(): UInt = littleEndian { int.toUInt() }
public fun ByteBuffer.readU64(): ULong = littleEndian { long.toULong() }
public fun ByteBuffer.readU128(): BigInteger = littleEndian {
    val arr = ByteArray(16)
    get(arr)
    BigInteger(arr.reversedArray())
}

public fun ByteBuffer.readByte(): Byte = littleEndian {
    get()
}

public fun ByteBuffer.readString(): String = littleEndian {
    val size = readCompactInteger().toInt()
    val ba = ByteArray(size)
    get(ba)
    ba.toString(charset = Charset.defaultCharset())
}

public fun ByteBuffer.readBoolean(): Boolean = littleEndian {
    return this.get() == 0x01.toByte()
}

public fun <T> ByteBuffer.readList(elementParser: ByteBuffer.() -> T): List<T> = littleEndian {
    val size = readCompactInteger().toInt()
    val list = ArrayList<T>(size)
    for (i in 0 until size) {
        list.add(this.elementParser())
    }
    list
}

public fun ByteBuffer.readCompactInteger(): BigInteger = littleEndian {
    val compactByte = this.get().toInt() and 0xff

    val compactLength = when (compactByte % 4) {
        0 -> 1
        1 -> 2
        2 -> 4
        else -> 5 + (compactByte - 3) / 4
    }

    return when (compactLength) {
        1 -> BigInteger.valueOf(compactByte.toLong() shr 2)
        2 -> {
            this.position(position() - 1)
            ((short.toInt() and 0xffff) shr 2).toBigInteger()
        }
        4 -> {
            val ba = ByteArray(compactLength - 1)
            get(ba)
            BigInteger((byteArrayOf(compactByte.toByte()) + ba).reversedArray()).divide(BigInteger.valueOf(4))
        }
        else -> {
            val ba = ByteArray(compactLength - 1)
            get(ba)
            BigInteger(ba.reversedArray())
        }
    }
}

public fun ByteBuffer.readByteArray(n: Int? = null): ByteArray = littleEndian {
    val ba = ByteArray(n ?: readCompactInteger().toInt())
    get(ba)
    ba
}

public fun ByteBuffer.readBytes(offset: Int): ByteArray = littleEndian {
    val ba = ByteArray(offset)
    get(ba)
    ba
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

public fun ByteBuffer.readPerquintill(): BigDecimal =
    BigDecimal(readU64().toString()).times(BigDecimal.valueOf(1, 18))