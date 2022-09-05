package acurast.codec.extensions

import org.bouncycastle.crypto.digests.Blake2bDigest

public fun ByteArray.toHex(): String = this.fold("") { str, it -> str + "%02x".format(it) }

public fun ByteArray.toU8a(): ByteArray {
    return this.size.toLong().toCompactU8a() + this
}

public fun ByteArray.trimTrailingZeros() : ByteArray = this.dropLastWhile { it == 0x00.toByte() }.toByteArray()
