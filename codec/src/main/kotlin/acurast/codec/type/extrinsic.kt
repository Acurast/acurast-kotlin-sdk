package acurast.codec.type

import acurast.codec.toCompactU8a
import acurast.codec.toU8a
import java.math.BigInteger

/**
 * Classes implementing this interface provide a 'toU8a' method to encode the
 * value as a Uint8Array as per the parity-codec specifications.
 */
public interface ToU8a {
    public fun toU8a(): ByteArray;
}

/**
 * An extrinsic method call.
 */
public interface ExtrinsicCall: ToU8a

/**
 * The extrinsic payload to be signed.
 *
 * @param method The extrinsic method call.
 * @param era The [[ExtrinsicEra]] (mortal or immortal) this signature applies to.
 * @param nonce A sequential signature counter of the extrinsic signer.
 * @param tip The tip amount to be given to the validator.
 * @param specVersion The specification version extracted from 'state_getRuntimeVersion'.
 * @param transactionVersion The transaction version extracted from 'state_getRuntimeVersion'.
 * @param genesisHash The hash of the first block.
 * @param blockHash The hash of a recent block.
 */
public data class ExtrinsicPayload(
        val method: ExtrinsicCall,
        val era: ExtrinsicEra,
        val nonce: Long,
        val tip: BigInteger,
        val specVersion: Long,
        val transactionVersion: Long,
        val genesisHash: ByteArray,
        val blockHash: ByteArray,
): ToU8a {
    override fun toU8a(): ByteArray {
        return method.toU8a() +
                era.toU8a() +
                nonce.toCompactU8a() +
                tip.toCompactU8a() +
                specVersion.toInt().toU8a() +
                transactionVersion.toInt().toU8a() +
                genesisHash +
                blockHash
    }
}

/**
 * An extrinsic.
 *
 * @param signature The signature used to validate the extrinsic method call.
 * @param method The extrinsic method call.
 * @param extrinsic_version The bytes representing the output of the script.
 */
public data class Extrinsic(
        val signature: ExtrinsicSignature,
        val method: ExtrinsicCall,
        val extrinsic_version: ByteArray = byteArrayOf(0x84.toByte()) // currently on version 4
): ToU8a {
    override fun toU8a(): ByteArray {
        val bytes = extrinsic_version + signature.toU8a() + method.toU8a();
        return bytes.size.toLong().toCompactU8a() + bytes;
    }
}
