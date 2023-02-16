package acurast.codec.extrinsic

import acurast.codec.extensions.toCompactU8a
import acurast.codec.extensions.toU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.MarketplaceAdvertisement
import acurast.codec.type.marketplace.ExecutionResult

/**
 * Extrinsic method call 'acknowledgeMatch'.
 *
 * @param callIndex The "acknowledgeMatch" call index (pallet index + call index).
 * @param jobId The identifier of the job being acknowledged.
 */
public data class AcknowledgeMatchCall(val callIndex: ByteArray, val jobId: JobIdentifier):
    ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex +
                jobId.requester.toU8a() +
                jobId.script.size.toLong().toCompactU8a() + jobId.script
    }
}

/**
 * Extrinsic method call 'advertise'.
 *
 * @param callIndex The "advertise" call index (pallet index + call index).
 * @param advertisement The advertisement information of the data processor.
 */
public data class AdvertiseCall(
    val callIndex: ByteArray,
    val advertisement: MarketplaceAdvertisement
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex + advertisement.toU8a()
    }
}

/**
 * Extrinsic method call 'report'.
 *
 * @param callIndex The "report" call index (pallet index + call index).
 * @param jobId The identifier of the job being reported.
 * @param executionResult The execution result of the job.
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