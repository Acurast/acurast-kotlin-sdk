package acurast.codec.extrinsic

import acurast.codec.toCompactU8a
import acurast.codec.type.ExtrinsicCall

private val FULFILL_CALL_INDEX: ByteArray = byteArrayOf(0x28, 0x03);

/**
 * @name FulfillCall
 * @description Extrinsic method call 'fulfill'.
 * @param script The ipfs url of the script executed.
 * @param payload The bytes representing the output of the script.
 * @param requester The job requester.
 */
public data class FulfillCall(
    val script: ByteArray,
    val payload: ByteArray,
    val requester: ByteArray,
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return FULFILL_CALL_INDEX +
                script.size.toLong().toCompactU8a() + script +
                payload.size.toLong().toCompactU8a() + payload +
                byteArrayOf(0x00) + requester
    }
}
