package acurast.codec.extensions

import acurast.codec.type.ToU8a
import java.math.BigInteger

public fun <T : ToU8a> List<T>.toU8a(withSize: Boolean = true): ByteArray {
    val size = if (withSize) this.size.toLong().toCompactU8a() else byteArrayOf()
    return size + this.fold(byteArrayOf()) { acc, i -> acc + i.toU8a() }
}

public fun BigInteger.toU8a(): ByteArray {
    val encoded = this.toByteArray()
    val padLeft = ByteArray(16 - encoded.size)
    return (padLeft + encoded).reversedArray()
}

public fun Boolean.toU8a(): ByteArray = byteArrayOf(if(this) 0x01 else 0x00)