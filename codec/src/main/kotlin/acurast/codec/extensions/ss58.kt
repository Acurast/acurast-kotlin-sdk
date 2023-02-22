package acurast.codec.extensions

import org.bitcoinj.core.Base58
import org.bouncycastle.crypto.digests.Blake2bDigest

/**
 * Encode address
 */
public fun ByteArray.toSS58(substrateID: Byte = 0x2a): String {
    val body = byteArrayOf(substrateID) + this
    val prefix = "SS58PRE".toByteArray();

    // https://github.com/paritytech/substrate/blob/master/primitives/core/src/crypto.rs
    val digest = Blake2bDigest(512)
    val checksum = ByteArray(digest.digestSize)
    digest.update(prefix, 0, prefix.size)
    digest.update(body, 0, body.size)
    digest.doFinal(checksum, 0)

    return Base58.encode((body + checksum.slice(0..1)))
}

/**
 * Decode address string
 */
public fun String.fromSS58(): ByteArray {
    val decoded = Base58.decode(this)
    return decoded.slice(1..decoded.size-3).toByteArray()
}