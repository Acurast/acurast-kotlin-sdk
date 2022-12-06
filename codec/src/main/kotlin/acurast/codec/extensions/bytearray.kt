package acurast.codec.extensions

public fun ByteArray.toHex(): String = this.fold("") { str, it -> str + "%02x".format(it) }

public fun ByteArray.toU8a(): ByteArray {
    return this.size.toLong().toCompactU8a() + this
}

public fun ByteArray.trimTrailingZeros() : ByteArray = this.dropLastWhile { it == 0x00.toByte() }.toByteArray()
