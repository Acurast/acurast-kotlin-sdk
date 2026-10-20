package acurast.codec.extensions

import org.bitcoinj.base.Base58
import org.bouncycastle.crypto.digests.Blake2bDigest

/**
 * Encode address
 */
public fun ByteArray.toSS58(substrateID: Byte = 0x2a): String = toSS58Address(substrateID.toInt())

/**
 * Encodes this public key or account id as an SS58 address with the network [prefix] (0 to 16383,
 * e.g. 42 for Acurast, 137 for Vara).
 */
public fun ByteArray.toSS58Address(prefix: Int): String {
    require(prefix in 0..16383) { "Invalid SS58 prefix $prefix" }
    val prefixBytes = if (prefix < 64) {
        byteArrayOf(prefix.toByte())
    } else {
        byteArrayOf(
            (((prefix and 0b1111_1100) shr 2) or 0b0100_0000).toByte(),
            ((prefix shr 8) or ((prefix and 0b11) shl 6)).toByte(),
        )
    }
    val body = prefixBytes + this
    val checksumPrefix = "SS58PRE".toByteArray()

    // https://github.com/paritytech/substrate/blob/master/primitives/core/src/crypto.rs
    val digest = Blake2bDigest(512)
    val checksum = ByteArray(digest.digestSize)
    digest.update(checksumPrefix, 0, checksumPrefix.size)
    digest.update(body, 0, body.size)
    digest.doFinal(checksum, 0)

    return Base58.encode(body + checksum.copyOfRange(0, 2))
}

/** Encodes these bytes as plain Base58 (Bitcoin alphabet, no checksum), e.g. a Solana address. */
public fun ByteArray.toBase58(): String = Base58.encode(this)

/**
 * Decode address string
 */
public fun String.fromSS58(): ByteArray {
    val decoded = Base58.decode(this)
    return decoded.slice(1..decoded.size-3).toByteArray()
}