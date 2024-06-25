package acurast.codec.extrinsic

import acurast.codec.extensions.readByte
import acurast.codec.extensions.readByteArray
import acurast.codec.extensions.toU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.ToU8a
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.MarketplaceAdvertisement
import acurast.codec.type.marketplace.ExecutionResult
import java.nio.ByteBuffer

/**
 * An interface used to identify the public keys of multiple supported curves.
 */
public sealed interface PublicKey: ToU8a {
    public val bytes: ByteArray

    public companion object {
        public fun read(bytes: ByteBuffer): PublicKey {
            val tag = Tag.read(bytes)
            val value = bytes.readByteArray()

            return when (tag) {
                Tag.Secp256r1 -> Secp256r1(value)
                Tag.Secp256k1 -> Secp256k1(value)
                Tag.Ed25519 -> Ed25519(value)
                Tag.Secp256r1Encryption -> Secp256r1Encryption(value)
                Tag.Secp256k1Encryption -> Secp256k1Encryption(value)
            }
        }
    }

    public enum class Tag(public val id: Byte) : ToU8a {
        Secp256r1(0),
        Secp256k1(1),
        Ed25519(2),
        Secp256r1Encryption(3),
        Secp256k1Encryption(4);

        override fun toU8a(): ByteArray = id.toU8a()

        public companion object {
            public fun read(bytes: ByteBuffer): Tag {
                val value = bytes.readByte()

                return entries.find { it.id == value } ?: throw RuntimeException("Unknown PublicKey tag ($value)")
            }
        }
    }

    private sealed interface Kind {
        val tag: Tag

        companion object {
            internal val values: List<Kind>
                get() = listOf(
                    Secp256r1,
                    Secp256k1,
                    Ed25519,
                )
        }
    }

    public data class Secp256r1(override val bytes: ByteArray) : PublicKey {
        public companion object : Kind {
            override val tag: Tag = Tag.Secp256r1
        }

        override fun toU8a(): ByteArray = tag.toU8a() + bytes.toU8a()
    }

    public data class Secp256k1(override val bytes: ByteArray): PublicKey {
        public companion object : Kind {
            override val tag: Tag = Tag.Secp256k1
        }

        override fun toU8a(): ByteArray = tag.toU8a() + bytes.toU8a()
    }

    public data class Ed25519(override val bytes: ByteArray): PublicKey {
        public companion object : Kind {
            override val tag: Tag = Tag.Ed25519
        }

        override fun toU8a(): ByteArray = tag.toU8a() + bytes.toU8a()
    }

    public data class Secp256r1Encryption(override val bytes: ByteArray) : PublicKey {
        public companion object : Kind {
            override val tag: Tag = Tag.Secp256r1Encryption
        }

        override fun toU8a(): ByteArray = tag.toU8a() + bytes.toU8a()
    }

    public data class Secp256k1Encryption(override val bytes: ByteArray): PublicKey {
        public companion object : Kind {
            override val tag: Tag = Tag.Secp256k1Encryption
        }

        override fun toU8a(): ByteArray = tag.toU8a() + bytes.toU8a()
    }
}

/**
 * Extrinsic method call 'acknowledgeMatch'.
 *
 * @param callIndex The "acknowledgeMatch" call index (pallet index + call index).
 * @param jobId The identifier of the job being acknowledged.
 * @param publicKeys The publicKeys that should be used to encrypt env variables.
 */
public data class AcknowledgeMatchCall(
    val callIndex: ByteArray,
    val jobId: JobIdentifier,
    val publicKeys: List<PublicKey>
): ExtrinsicCall {

    override fun toU8a(): ByteArray {
        return callIndex + jobId.toU8a() + publicKeys.toU8a(withSize = true)
    }
}

/**
 * Extrinsic method call 'acknowledgeExecutionMatch'.
 *
 * @param callIndex The "acknowledgeExecutionMatch" call index (pallet index + call index).
 * @param jobId The identifier of the job being acknowledged.
 * @param executionIndex The execution index to acknwoledge in a competing fashion.
 * @param publicKeys The publicKeys that should be used to encrypt env variables.
 */
public data class AcknowledgeExecutionMatchCall(
    val callIndex: ByteArray,
    val jobId: JobIdentifier,
    val executionIndex: UInt,
    val publicKeys: List<PublicKey>
): ExtrinsicCall {

    override fun toU8a(): ByteArray {
        return callIndex + jobId.toU8a() + executionIndex.toLong().toU8a() + publicKeys.toU8a(withSize = true)
    }
}

/**
 * Extrinsic method call 'advertise'.
 *
 * @param callIndex The "advertise" call index (pallet index + call index).
 * @param advertisement The advertisement information of the data processor.
 */
public data class AdvertiseCall(
    val callIndex: ByteArray,
    val advertisement: MarketplaceAdvertisement
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex + advertisement.toU8a()
    }
}

/**
 * Extrinsic method call 'report'.
 *
 * @param callIndex The "report" call index (pallet index + call index).
 * @param jobId The identifier of the job being reported.
 * @param executionResult The execution result of the job.
 */
public data class ReportCall(
    val callIndex: ByteArray,
    val jobId: JobIdentifier,
    val executionResult: ExecutionResult
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex +
                jobId.toU8a() +
                executionResult.toU8a()
    }
}

/**
 * Extrinsic method call 'finalizeJob'.
 *
 * @param callIndex The "finalizeJob" call index (pallet index + call index).
 * @param jobId The identifier of the job being reported.
 */
public data class FinalizeJob(
    val callIndex: ByteArray,
    val jobId: JobIdentifier
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex + jobId.toU8a()
    }
}