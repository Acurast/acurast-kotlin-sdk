package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.MultiAddress
import acurast.codec.type.readAccountId
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer

/**
 * The structure of a Job Assignment.
 */
public data class StoredJobAssignment(
    public val processor: MultiAddress,
    public val requester: MultiAddress,
    public val script: ByteArray,
    public val slot: Int,
) {
    public companion object {
        public fun read(l: List<String>): StoredJobAssignment {
            val slot = ByteBuffer.wrap(l[1].hexToBa()).readCompactInteger()
            val keys = ByteBuffer.wrap(l[0].hexToBa())
            keys.skip(48); // Skip <pallet_name>, <method_name>, <processor_hash>
            val processor = keys.readAccountId()
            keys.skip(16); // Skip <requester_hash>
            val requester = keys.readAccountId()
            val script = keys.readByteArray()
            return StoredJobAssignment(processor, requester, script, slot)
        }
    }
}

/**
 * The structure of a Job Match.
 */
public data class PalletAcurastMarketplaceAssignment(
    public val processor: MultiAddress,
    public val requester: MultiAddress,
    public val script: ByteArray,
    public val slot: Int,
    public val feePerExecution: MultiAssetV1,
    public val acknowledged: Boolean,
) {
    public companion object {
        public fun read(l: List<String>): PalletAcurastMarketplaceAssignment {
            val buffer1 = ByteBuffer.wrap(l[0].hexToBa())
            buffer1.skip(48); // Skip <pallet_name>, <method_name>, <processor_hash>
            val processor = buffer1.readAccountId()
            buffer1.skip(16); // Skip <requester_hash>
            val requester = buffer1.readAccountId()
            val script = buffer1.readByteArray()

            val buffer2 = ByteBuffer.wrap(l[1].hexToBa())

            return PalletAcurastMarketplaceAssignment(
                processor,
                requester,
                script,
                slot = buffer2.readCompactInteger(),
                feePerExecution = MultiAssetV1.read(buffer2),
                acknowledged = buffer2.readBoolean()
            )
        }
    }
}