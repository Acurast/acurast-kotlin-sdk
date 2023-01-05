package acurast.codec.extrinsic

import acurast.codec.extensions.toCompactU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.acurast.JobIdentifier

/**
 * Extrinsic method call 'report'.
 *
 * @param jobId The identifier of the job being reported.
 */
public data class ReportCall(val callIndex: ByteArray, val jobId: JobIdentifier):
    ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex +
                jobId.requester.toU8a() +
                jobId.script.size.toLong().toCompactU8a() + jobId.script
    }
}