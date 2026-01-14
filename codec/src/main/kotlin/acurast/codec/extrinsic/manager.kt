package acurast.codec.extrinsic

import acurast.codec.extensions.toU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.compute.Metrics
import acurast.codec.type.acurast.AttestationChain
import acurast.codec.type.manager.Platform
import acurast.codec.type.manager.ProcessorPairing

/**
 * Extrinsic method call 'heartbeat' for the processor-manager pallet.
 *
 * @param callIndex The "heartbeat" call index (pallet index + call index).
 */
public data class HeartbeatCall(val callIndex: ByteArray) : ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex;
    }
}

/**
 * Extrinsic method call 'heartbeatWithVersion' for the processor-manager pallet.
 *
 * @param callIndex The "heartbeat" call index (pallet index + call index).
 * @param platform
 * @param buildNumber The build number of the processor app running on the device.
 */
public data class HeartbeatWithVersionCall(
    val callIndex: ByteArray,
    val platform: Platform,
    val buildNumber: UInt,
) : ExtrinsicCall {
    override fun toU8a(): ByteArray = callIndex + platform.value.toU8a() + buildNumber.toU8a()
}

/**
 * Extrinsic method call 'heartbeatWithMetrics' for the processor-manager pallet.
 *
 * @param callIndex The "heartbeat" call index (pallet index + call index).
 * @param platform
 * @param buildNumber The build number of the processor app running on the device.
 * @param metrics
 */
public data class HeartbeatWithMetricsCall(
    val callIndex: ByteArray,
    val platform: Platform,
    val buildNumber: UInt,
    val metrics: List<Metrics>
) : ExtrinsicCall {
    override fun toU8a(): ByteArray = callIndex + platform.value.toU8a() + buildNumber.toU8a() + metrics.toU8a()
}

/**
 * Extrinsic method call 'pairWithManager' for the processor-manager pallet.
 *
 * @param callIndex The "pairWithManager" call index (pallet index + call index).
 * @param pairing The pairing payload used to assign a manager to a device.
 */
public data class PairWithManagerCall(
    val callIndex: ByteArray,
    val pairing: ProcessorPairing
) : ExtrinsicCall {
    override fun toU8a(): ByteArray = callIndex + pairing.toU8a()
}

public data class OnboardCall(
    val callIndex: ByteArray,
    val pairing: ProcessorPairing,
    val multi: Boolean,
    val attestationChain: AttestationChain,
) : ExtrinsicCall {
    override fun toU8a(): ByteArray = callIndex + pairing.toU8a() + multi.toU8a() + attestationChain.toU8a()
}