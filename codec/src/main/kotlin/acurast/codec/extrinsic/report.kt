package acurast.codec.extrinsic

import acurast.codec.extensions.toCompactU8a
import acurast.codec.extensions.toU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.marketplace.ExecutionResult

/**
 * Extrinsic method call 'report'.
 *
 * @param jobId The identifier of the job being reported.
 */
public data class ReportCall(
    val callIndex: ByteArray,
    val jobId: JobIdentifier,
    val last: Boolean,
    val executionResult: ExecutionResult
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex +
                jobId.requester.toU8a() +
                jobId.script.size.toLong().toCompactU8a() + jobId.script +
                last.toU8a() +
                executionResult.toU8a()
    }
}