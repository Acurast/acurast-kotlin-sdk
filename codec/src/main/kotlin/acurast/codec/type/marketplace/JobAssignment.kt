package acurast.codec.type.marketplace

import acurast.codec.extensions.*
import acurast.codec.type.*
import acurast.codec.type.acurast.JobIdentifier
import java.nio.ByteBuffer

/**
 * The structure of a Job Assignment.
 */
public data class JobAssignment(
    public val processor: AccountId32,
    public val jobId: JobIdentifier,
    public val slot: Int,
    public val feePerExecution: MultiAssetV1,
    public val acknowledged: Boolean,
    public val sla: SLA
) {
    public companion object {
        public fun read(l: List<String>): JobAssignment {
            val key = ByteBuffer.wrap(l[0].hexToBa())
            key.skip(48); // Skip <pallet_name>, <method_name>, <processor_hash>
            val processor = key.readAccountId32()
            key.skip(16); // Skip <requester_hash>
            val requester = key.readAccountId32()
            val script = key.readByteArray()

            val value = ByteBuffer.wrap(l[1].hexToBa())

            return JobAssignment(
                processor,
                jobId = JobIdentifier(requester, script),
                slot = value.readCompactInteger(),
                feePerExecution = MultiAssetV1.read(value),
                acknowledged = value.readBoolean(),
                sla = SLA.read(value)
            )
        }
    }
}

/**
 * Keeps track of the SLA during and after a job's schedule is completed.
 */
public data class SLA(
    public val total: Long,
    public val met: Long
) {
    public companion object {
        public fun read(buffer: ByteBuffer): SLA {
            return SLA(
                buffer.long,
                buffer.long
            )
        }
    }
}