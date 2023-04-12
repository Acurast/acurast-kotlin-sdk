package acurast.codec.extensions

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

public class ScaleDecoderException(msg: String?) : Exception(msg)

public inline fun <reified T> ByteBuffer.littleEndian(decoder: ByteBuffer.() -> T): T {
    order(ByteOrder.LITTLE_ENDIAN)
    return decoder()
}

@OptIn(ExperimentalUnsignedTypes::class)
public fun ByteBuffer.readU32(): UInt = littleEndian { int.toUInt() }

public fun ByteBuffer.readU128(): BigInteger = littleEndian {
    val arr = ByteArray(16)
    get(arr)
    BigInteger(arr.reversedArray())
}

public fun ByteBuffer.readByte(): Byte = littleEndian {
    get()
}

public fun ByteBuffer.readString(): String = littleEndian {
    val size = readCompactInteger()
    val ba = ByteArray(size)
    get(ba)
    ba.toString(charset = Charset.defaultCharset())
}

public fun ByteBuffer.readBoolean(): Boolean = littleEndian {
    return this.get() == 0x01.toByte()
}

public fun <T> ByteBuffer.readList(elementParser: ByteBuffer.() -> T): List<T> = littleEndian {
    val size = readCompactInteger()
    val list = ArrayList<T>(size)
    for (i in 0 until size) {
        list.add(this.elementParser())
    }
    list
}

public fun ByteBuffer.readCompactInteger(): Int = littleEndian {
    val byte = this.get().toInt() and 0xff
    if (byte and 0b11 == 0x00) {
        return (byte shr 2)
    }
    if (byte and 0b11 == 0x01) {
        this.position(position() - 1)
        return ((short.toInt() and 0xffff) shr 2)
    }
    if (byte and 0b11 == 0x02) {
        this.position(position() - 1)
        return (int shr 2)
    }
    throw ScaleDecoderException("compact mode not supported")
}

public fun ByteBuffer.readByteArray(): ByteArray = littleEndian {
    val ba = ByteArray(readCompactInteger())
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

public fun ByteBuffer.skip(offset: Int): Unit = littleEndian {
    get(ByteArray(offset))
}

public fun ByteBuffer.readCompactU128(): BigInteger = littleEndian {
    this.order(ByteOrder.LITTLE_ENDIAN)
    val byte = this.get().toInt() and 0xff
    if (byte and 0b11 == 0x00) {
        return (byte shr 2).toBigInteger()
    }
    if (byte and 0b11 == 0x01) {
        this.position(position() - 1)
        return ((short.toInt() and 0xffff) shr 2).toBigInteger()
    }
    if (byte and 0b11 == 0x02) {
        this.position(position() - 1)
        return (int shr 2).toBigInteger()
    }

    val len = byte shr 2
    val ba = ByteArray(len)
    get(ba)
    return BigInteger(ba.reversedArray())
}