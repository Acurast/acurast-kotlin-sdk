package acurast.codec.extrinsic

import acurast.codec.extensions.toCompactU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.acurast.AttestationChain

/**
 * Extrinsic method call 'submitAttestation'.
 * Submits an attestation given a valid certificate chain.
 *
 * @param callIndex The "submitAttestation" call index (pallet index + call index).
 * @param attestationChain A list of certificates in bytes. The list must be ordered, starting from one of the known [trusted root certificates](https://developer.android.com/training/articles/security-key-attestation#root_certificate).
 */
public data class SubmitAttestationCall(val callIndex: ByteArray, val attestationChain: AttestationChain):
    ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex + attestationChain.toU8a()
    }
}

/**
 * Extrinsic method call 'fulfill'.
 *
 * @param callIndex The "fulfill" call index (pallet index + call index).
 * @param script The ipfs url of the script executed.
 * @param payload The bytes representing the output of the script.
 */
public data class FulfillCall(
    val callIndex: ByteArray,
    val script: ByteArray,
    val payload: ByteArray
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex +
                script.size.toLong().toCompactU8a() + script +
                payload.size.toLong().toCompactU8a() + payload
    }
}