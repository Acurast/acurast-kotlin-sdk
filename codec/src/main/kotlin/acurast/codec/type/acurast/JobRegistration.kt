package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.*
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.nio.ByteBuffer

/**
 * The structure of a Job Registration.
 */
public data class JobRegistration(
    // The script to execute. It is a vector of bytes representing a utf8 string. The string needs to be a ipfs url that points to the script.
    public val script: ByteArray,
    // An optional array of [AccountId32] allowed to fulfill the job. If the value is [null], then all sources are allowed.
    public val allowedSources: List<AccountId32>? = null,
    // A boolean indicating if only verified sources can fulfill the job. A verified source is one that has provided a valid key attestation.
    public val allowOnlyVerifiedSources: Boolean,
    // The schedule describing the desired (multiple) execution(s) of the script.
    public val schedule: JobSchedule,
    // Maximum memory bytes used during a single execution of the job.
    public val memory: Int,
    // The modules required for the job.
    public val networkRequests: Int,
    // Maximum storage bytes used during the whole period of the job's executions.
    public val storage: Int,
    //
    public val requiredModules: List<JobModule>,
    // Extra parameters.
    public val extra: JobRegistrationExtra,
) {
    public companion object {
        public fun read(value: ByteBuffer, apiVersion: UInt): JobRegistration {
            val script = value.readByteArray()
            val allowedSources = value.readOptional { readList { readAccountId32() } }
            val allowOnlyVerifiedSources = value.readBoolean()
            val schedule = JobSchedule.read(value)
            val memory = value.int
            val networkRequests = value.int
            val storage = value.int
            val requiredModules = value.readList { JobModule.read(this) }
            val extra =
                if (apiVersion > 0u) JobRegistrationExtra.read(value) else JobRegistrationExtra(
                    JobRequirements(
                        AssignmentStrategy.Single(),
                        0,
                        BigInteger.ZERO,
                        null,
                        null,
                        Runtime.NodeJS,
                    )
                )

            return JobRegistration(
                script,
                allowedSources,
                allowOnlyVerifiedSources,
                schedule,
                memory,
                networkRequests,
                storage,
                requiredModules,
                extra
            )
        }
    }
}


/**
 * The structure of a Job Schedule.
 */
public data class JobSchedule(
    // An upperbound for the duration of one execution of the script in milliseconds.
    public val duration: Long,
    // Start time in milliseconds since Unix Epoch.
    public val startTime: Long,
    // End time in milliseconds since Unix Epoch.
    //
    // Represents the end time (exclusive) in milliseconds since Unix Epoch
    // of the period in which a job execution can start, relative to `startDelay == 0`, independent of `duration`.
    //
    // Hence the latest possible start time is `endTime + startDelay - 1`.
    // and all executions fit into `[startTime + startDelay, endTime + duration + startDelay]`.
    //
    // (startDelay is the actual start delay chosen within `[0, maxStartDelay]` during assigning the job to an available processor)
    public val endTime: Long,
    // Interval at which to repeat execution in milliseconds.
    public val interval: Long,
    // Maximum delay before each execution in milliseconds.
    public val maxStartDelay: Long,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): JobSchedule {
            return JobSchedule(
                buffer.long,
                buffer.long,
                buffer.long,
                buffer.long,
                buffer.long
            )
        }
    }
}


/**
 * The structure of a extra field in JobRegistration.
 */
public data class JobRegistrationExtra(
    public val requirements: JobRequirements,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): JobRegistrationExtra {
            return JobRegistrationExtra(JobRequirements.read(buffer))
        }
    }
}


/**
 * The structure of a AssignmentStrategy.
 */
public data class JobRequirements(
    public val assignmentStrategy: AssignmentStrategy,
    public val slots: Byte,
    public val reward: BigInteger,
    public val minReputation: BigInteger?,
    public val processorVersion: ProcessorVersionRequirements?,
    public val runtime: Runtime,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): JobRequirements {
            val assignmentStrategy = AssignmentStrategy.read(buffer)
            val slots = buffer.readByte()
            val reward = buffer.readU128()
            val minReputation = buffer.readOptional { readU128() }
            val processorVersion = buffer.readOptional { ProcessorVersionRequirements.read(this) }
            val runtime = Runtime.read(buffer)

            return JobRequirements(
                assignmentStrategy,
                slots,
                reward,
                minReputation,
                processorVersion,
                runtime,
            )
        }
    }
}

/**
 * Specifier of execution(s) to be assigned in a `JobAssignment`.
 */
public sealed interface AssignmentStrategy : ToU8a {
    public val id: Byte

    public data class Single(val plannedExecutions: List<PlannedExecution>? = null) : AssignmentStrategy {
        override val id: Byte = ID

        public companion object {
            internal const val ID: Byte = 0
        }
    }
    public data object Competing : AssignmentStrategy {
        override val id: Byte = 1
    }

    override fun toU8a(): ByteArray = this.id.toU8a()

    public companion object {
        public fun read(buffer: ByteBuffer): AssignmentStrategy =
            when (val id = buffer.readByte()) {
                Single.ID -> {
                    val plannedExecutions = buffer.readOptional {
                        readList { PlannedExecution.read(this) }
                    }

                    Single(plannedExecutions)
                }
                Competing.id -> Competing
                else -> throw UnsupportedEncodingException("Unknown AssignmentStrategy $id.")
            }
    }
}

public data class PlannedExecution(val source: AccountId32, val startDelay: ULong) {
    public companion object {
        public fun read(buffer: ByteBuffer): PlannedExecution {
            val source = buffer.readAccountId32()
            val startDelay = buffer.readU64()

            return PlannedExecution(source, startDelay)
        }
    }
}

public sealed interface ProcessorVersionRequirements {
    public val id: Byte

    public data class Min(public val platform: UInt, public val buildNumber: UInt) : ProcessorVersionRequirements {
        override val id: Byte = ID

        public companion object {
            internal const val ID: Byte = 0

            public fun read(buffer: ByteBuffer): Min {
                val platform = buffer.readU32()
                val buildNumber = buffer.readU32()

                return Min(platform, buildNumber)
            }
        }
    }

    public companion object {
        public fun read(buffer: ByteBuffer): ProcessorVersionRequirements =
            when (val id = buffer.readByte()) {
                Min.ID -> Min.read(buffer)
                else -> throw UnsupportedEncodingException("Unknown ProcessorVersionRequirements $id.")
            }
    }
}

public enum class Runtime(public val id: Byte) {
    NodeJS(0),
    NodeJSWithBundle(1);

    public companion object {
        public fun read(buffer: ByteBuffer): Runtime =
            when (val id = buffer.readByte()) {
                NodeJS.id -> NodeJS
                NodeJSWithBundle.id -> NodeJSWithBundle
                else -> throw UnsupportedEncodingException("Unknown Runtime $id.")
            }
    }
}
