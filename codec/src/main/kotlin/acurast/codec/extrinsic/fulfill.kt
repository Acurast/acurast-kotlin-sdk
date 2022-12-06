package acurast.codec.extrinsic

import acurast.codec.extensions.toCompactU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.MultiAddress

/**
 * Extrinsic method call 'fulfill'.
 *
 * @param callIndex The "fulfill" call index (pallet index + call index).
 * @param script The ipfs url of the script executed.
 * @param payload The bytes representing the output of the script.
 */
public data class FulfillCall(
    val callIndex: ByteArray,
    val script: ByteArray,
    val payload: ByteArray
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex +
                script.size.toLong().toCompactU8a() + script +
                payload.size.toLong().toCompactU8a() + payload
    }
}
