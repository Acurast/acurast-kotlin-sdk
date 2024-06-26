package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.*
import java.io.UnsupportedEncodingException
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
                    JobRequirements(AssignmentStrategy.Single)
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
) {
    public companion object {
        public fun read(buffer: ByteBuffer): JobRequirements {
            return JobRequirements(
                AssignmentStrategy.read(buffer)
                // ignore subsequent fields for now
            )
        }
    }
}

/**
 * Specifier of execution(s) to be assigned in a `JobAssignment`.
 */
public enum class AssignmentStrategy(public val id: Byte) : ToU8a {
    Single(0),
    Competing(1);

    override fun toU8a(): ByteArray = this.id.toU8a()

    public companion object {
        public fun read(buffer: ByteBuffer): AssignmentStrategy {
            return when (val id = buffer.readByte()) {
                Single.id -> Single
                Competing.id -> Competing
                else -> throw UnsupportedEncodingException("Unknown AssignmentStrategy $id.")
            }
        }
    }
}


