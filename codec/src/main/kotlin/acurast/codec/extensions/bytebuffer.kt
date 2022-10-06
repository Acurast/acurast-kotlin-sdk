package acurast.codec.extensions

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

public class ScaleParserException(msg: String?) : Exception(msg)

@OptIn(ExperimentalUnsignedTypes::class)
public fun ByteBuffer.readU32(): UInt = order(ByteOrder.LITTLE_ENDIAN).int.toUInt()

public fun ByteBuffer.readString(): String {
    this.order(ByteOrder.LITTLE_ENDIAN)
    val size = readCompactInteger()
    val ba = ByteArray(size)
    get(ba)
    return ba.toString(charset = Charset.defaultCharset())
}

public fun <T> ByteBuffer.readList(elementParser: ByteBuffer.() -> T): List<T> {
    val size = readCompactInteger()
    val list = ArrayList<T>(size)
    for (i in 0 until size) {
        list.add(this.elementParser())
    }
    return list
}

public fun ByteBuffer.readCompactInteger(): Int {
    this.order(ByteOrder.LITTLE_ENDIAN)
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
    throw ScaleParserException("compact mode not supported")
}

public fun ByteBuffer.readByteArray(): ByteArray {
    this.order(ByteOrder.LITTLE_ENDIAN)
    val size = readCompactInteger()
    val ba = ByteArray(size)
    get(ba)
    return ba
}
