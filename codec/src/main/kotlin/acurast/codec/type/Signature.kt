package acurast.codec.type

import acurast.codec.extensions.readByte
import acurast.codec.extensions.readByteArray
import acurast.codec.extensions.toCompactU8a
import acurast.codec.extensions.toU8a
import java.math.BigInteger
import java.nio.ByteBuffer

/**
 * The payload signature of a given extrinsic.
 */
public interface ExtrinsicPayloadSignature: ToU8a

/**
 * Supported elliptic curves.
 */
public enum class CurveKind(public val id: Int) {
    Ed25519(0),
    Sr25519(1),
    Secp256k1(2),
    Secp256r1(3),
    P256WithAuthData(4),
    Ed25519WithPrefix(5),
    K256WithPrefix(6),
    Ed25519WithBase64(7),
    ;

    public companion object {
        public fun read(value: ByteBuffer): CurveKind =
            when (value.readByte().toInt()) {
                0 -> Ed25519
                1 -> Sr25519
                2 -> Secp256k1
                3 -> Secp256r1
                4 -> P256WithAuthData
                5 -> Ed25519WithPrefix
                6 -> K256WithPrefix
                7 -> Ed25519WithBase64
                else -> throw IllegalArgumentException("Unknown CurveKind")
            }
    }
}

/**
 * The extrinsic signature with the curve identifier, which
 * is used in the scale encoding.
 */
public class MultiSignature(
    private val kind: CurveKind,
    private val signature: ByteArray
): ExtrinsicPayloadSignature {
    override fun toU8a(): ByteArray {
        return kind.id.toByte().toU8a() + signature
    }

    public companion object {
        public fun read(value: ByteBuffer): MultiSignature {
            val kind = CurveKind.read(value)
            val signature = value.readByteArray(value.remaining())

            return MultiSignature(kind, signature)
        }
    }
}

/**
 * Extrinsic signature content.
 *
 * @param signer The [[MultiAddress]] of who signed the extrinsic.
 * @param signature The [[ExtrinsicPayloadSignature]] extrinsic signature.
 * @param era The [[ExtrinsicEra]] (mortal or immortal) this signature applies to.
 * @param nonce A sequential signature counter of the extrinsic signer.
 * @param tip The tip amount to be given to the validator.
 */
public data class ExtrinsicSignature(
    val signer: MultiAddress,
    val signature: ExtrinsicPayloadSignature,
    val era: ExtrinsicEra,
    val nonce: Long,
    val tip: BigInteger
): ToU8a {
    override fun toU8a(): ByteArray {
        return signer.toU8a() +
                signature.toU8a() +
                era.toU8a() +
                nonce.toCompactU8a() +
                tip.toCompactU8a();
    }
}
