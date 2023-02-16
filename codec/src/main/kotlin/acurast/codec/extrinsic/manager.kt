package acurast.codec.extrinsic

import acurast.codec.type.ExtrinsicCall
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