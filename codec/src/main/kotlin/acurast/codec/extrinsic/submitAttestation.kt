package acurast.codec.extrinsic

import acurast.codec.extensions.toCompactU8a
import acurast.codec.type.ExtrinsicCall

private val CALL_INDEX: ByteArray = byteArrayOf(0x28, 0x05);

/**
 * Extrinsic method call 'submitAttestation'.
 * Submits an attestation given a valid certificate chain.
 *
 * @param attestationChain A list of certificates in bytes. The list must be ordered, starting from one of the known [trusted root certificates](https://developer.android.com/training/articles/security-key-attestation#root_certificate).
 */
public data class SubmitAttestationCall(val attestationChain: List<ByteArray>): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return CALL_INDEX + attestationChain.size.toLong().toCompactU8a() + attestationChain
            .fold(byteArrayOf()) { acc, cert -> acc + cert.size.toLong().toCompactU8a() + cert }
    }
}
