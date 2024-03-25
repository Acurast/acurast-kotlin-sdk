package acurast.codec.type.marketplace

import acurast.codec.extensions.*
import acurast.codec.extrinsic.PublicKey
import acurast.codec.type.*
import acurast.codec.type.acurast.JobIdentifier
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.nio.ByteBuffer

/**
 * The structure of a Job Assignment.
 */
public data class JobAssignment(
    public val processor: AccountId32,
    public val jobId: JobIdentifier,
    public val slot: Int,
    public val startDelay: Long,
    public val feePerExecution: UInt128,
    public val acknowledged: Boolean,
    public val sla: SLA,
    public val publicKeys: List<PublicKey>,
    public val executionSpecifier: ExecutionSpecifier,
) {
    public companion object {
        public fun read(l: List<String>, apiVersion: UInt): JobAssignment {
            val key = ByteBuffer.wrap(l[0].hexToBa())
            key.readBytes(48); // Skip <pallet_name>, <method_name>, <processor_hash>
            val processor = key.readAccountId32()
            key.readBytes(16); // Skip <requester_hash>
            val jobId = JobIdentifier.read(key)

            val value = ByteBuffer.wrap(l[1].hexToBa())

            val slot = value.readByte().toInt()
            val startDelay = value.long
            val feePerExecution = UInt128(value.readU128())
            val acknowledged = value.readBoolean()
            val sla = SLA.read(value)
            val publicKeys = value.readList { PublicKey.read(this) }

            val executionSpecifier = if (apiVersion > 0u)
                 ExecutionSpecifier.read(value) else ExecutionSpecifier(ExecutionSpecifierKind.All)

            return JobAssignment(
                processor,
                jobId = jobId,
                slot,
                startDelay,
                feePerExecution,
                acknowledged,
                sla,
                publicKeys,
                executionSpecifier
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

/**
 * Specifier of execution(s) to be assigned in a `JobAssignment`.
 */
public enum class ExecutionSpecifierKind(public val id: Byte) : ToU8a {
    All(0),
    Index(1);

    override fun toU8a(): ByteArray = this.id.toU8a()

    public companion object {
        public fun read(buffer: ByteBuffer): ExecutionSpecifierKind {
            return when (val id = buffer.readByte()) {
                All.id -> All
                Index.id -> Index
                else -> throw UnsupportedEncodingException("Unknown ExecutionSpecifier $id.")
            }
        }
    }
}

public data class ExecutionSpecifier(
    public val kind: ExecutionSpecifierKind,
    public val index: BigInteger? = null,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): ExecutionSpecifier {
            return when(ExecutionSpecifierKind.read(buffer)) {
                ExecutionSpecifierKind.All -> ExecutionSpecifier(
                    kind = ExecutionSpecifierKind.All,
                )
                ExecutionSpecifierKind.Index -> ExecutionSpecifier(
                    kind = ExecutionSpecifierKind.Index,
                    index = buffer.readCompactInteger()
                )
            }
        }
    }
}