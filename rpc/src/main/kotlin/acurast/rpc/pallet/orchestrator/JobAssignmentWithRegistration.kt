package acurast.rpc.pallet.orchestrator

import acurast.codec.extensions.*
import acurast.codec.type.*
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.JobModule
import acurast.codec.type.acurast.JobRegistration
import acurast.codec.type.acurast.JobSchedule
import acurast.codec.type.acurast.MultiOrigin
import acurast.codec.type.marketplace.JobAssignment
import acurast.codec.type.marketplace.SLA
import io.ktor.util.toUpperCasePreservingASCIIRules
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * The structure of a Job Assignment.
 */
public data class JobAssignmentWithRegistration(
    public val job: JobRegistration,
    public val assignment: JobAssignment,
) {
    public companion object {
        public fun fromJson(
            processor: AccountId32,
            json: JSONObject
        ): JobAssignmentWithRegistration {
            val job_id = JobIdentifier.fromJson(json.getJSONArray("jobId"))
            val job = JobRegistration.fromJson(json.getJSONObject("job"))
            val assignment =
                JobAssignment.fromJson(processor, job_id, json.getJSONObject("assignment"))

            return JobAssignmentWithRegistration(
                job,
                assignment,
            )
        }
    }
}

private fun JobIdentifier.Companion.fromJson(json: JSONArray): JobIdentifier {
    return JobIdentifier(
        origin = MultiOrigin.fromJson(json.getJSONObject(0)),
        id = json.getBigInteger(1),
    )
}

private fun MultiOrigin.Companion.fromJson(json: JSONObject): MultiOrigin {
    // parsing an enum from JSON such as {"acurast":"5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"}
    val key = json.keys().next()
    val kind = MultiOrigin.Kind.valueOf(key.replaceFirstChar(Char::uppercaseChar))
    return when (kind) {
        MultiOrigin.Kind.Acurast -> MultiOrigin(
            kind = kind,
            source = json.getString(key).fromSS58(),
        )

        MultiOrigin.Kind.Tezos -> MultiOrigin(
            kind = kind,
            source = json.getJSONArray(key).toU8a()
        )
    }
}

private fun JobRegistration.Companion.fromJson(json: JSONObject): JobRegistration {
    val script = json.getJSONArray("script").toU8a()
    val allowedSources =
        json.optJSONArray("allowedSources")?.map { AccountId32((it as String).fromSS58()) }
    val allowOnlyVerifiedSources = json.getBoolean("allowOnlyVerifiedSources")
    val schedule = JobSchedule.fromJson(json.getJSONObject("schedule"))
    val memory = json.getInt("memory")
    val networkRequests = json.getInt("networkRequests")
    val storage = json.getInt("storage")
    val requiredModules =
        json.getJSONArray("requiredModules").map { JobModule.fromJson(it as String) }
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

private fun JobSchedule.Companion.fromJson(json: JSONObject): JobSchedule {
    return JobSchedule(
        duration = json.getLong("duration"),
        startTime = json.getLong("startTime"),
        endTime = json.getLong("endTime"),
        interval = json.getLong("interval"),
        maxStartDelay = json.getLong("maxStartDelay"),
    )
}

private fun JobModule.Companion.fromJson(json: String): JobModule {
    return when (json) {
        "dataEncryption" -> JobModule.DataEncryption
        else -> throw UnsupportedEncodingException("Unknown JobModule $json.")
    }
}

private fun SLA.Companion.fromJson(json: JSONObject): SLA {
    return SLA(
        total = json.getLong("total"),
        met = json.getLong("met"),
    )
}

private fun JobAssignment.Companion.fromJson(
    processor: AccountId32,
    jobId: JobIdentifier,
    json: JSONObject
): JobAssignment {
    return JobAssignment(
        processor,
        jobId,
        slot = json.getInt("slot"),
        startDelay = json.getLong("startDelay"),
        feePerExecution = UInt128(json.getBigInteger("feePerExecution")),
        acknowledged = json.getBoolean("acknowledged"),
        sla = SLA.fromJson(json.getJSONObject("sla"))
    )
}

public fun JSONArray.toU8a(): ByteArray = this.foldIndexed(ByteArray(this.length())) { i, a, v ->
    a.apply {
        set(
            i,
            (v as Int).toByte()
        )
    }
}
