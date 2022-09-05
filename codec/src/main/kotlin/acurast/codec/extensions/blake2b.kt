package acurast.codec.extensions

import org.bouncycastle.crypto.digests.Blake2bDigest

public fun ByteArray.blake2b(hashSize: Int): ByteArray {
    val digest = Blake2bDigest(hashSize)
    val hash = ByteArray(digest.digestSize)
    digest.update(this, 0, size)
    digest.doFinal(hash, 0)
    return hash
}