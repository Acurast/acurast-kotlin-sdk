package acurast.codec.type.manager

import acurast.codec.extensions.readU128
import acurast.codec.type.*
import java.nio.ByteBuffer

/**
 * This structure represents the pairing payload used to assign a manager to a device.
 */
public data class ProcessorPairing(val account: AccountId32, val proof: Option<Proof>): ToU8a {
    /**
     * This structure represents the pairing proof used to assign a manager to a device.
     */
    public data class Proof(val timestamp: UInt128, val signature: MultiSignature): ToU8a {
        override fun toU8a(): ByteArray = timestamp.toU8a() + signature.toU8a()

        public companion object {
            public fun read(value: ByteBuffer): Proof {
                val timestamp = value.readU128()
                val signature = MultiSignature.read(value)

                return Proof(UInt128(timestamp), signature)
            }
        }
    }

    override fun toU8a(): ByteArray = account.toU8a() + proof.toU8a()
}

