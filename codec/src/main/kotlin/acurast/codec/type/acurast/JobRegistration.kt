package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.*
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
    public val extra: ByteArray
) {
    public companion object {
        public fun read(value: ByteBuffer): JobRegistration {
            val script = value.readByteArray()
            val allowedSources = value.readOptional { readList { readAccountId32() } }
            val allowOnlyVerifiedSources = value.readBoolean()
            val schedule = JobSchedule.read(value)
            val memory = value.int
            val networkRequests = value.int
            val storage = value.int
            val requiredModules = value.readList { JobModule.read(this) }
            val extra = byteArrayOf() // TODO

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
    // Represents the latest point in time where a job execution can end, assuming the worst-case `duration`.
    // Means every job needs to fit into `[startTime, endTime]`,
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
