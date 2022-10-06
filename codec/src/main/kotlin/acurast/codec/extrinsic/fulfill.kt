package acurast.codec.extrinsic

import acurast.codec.extensions.toCompactU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.MultiAddress

private val CALL_INDEX: ByteArray = byteArrayOf(0x28, 0x04);

/**
 * Extrinsic method call 'fulfill'.
 *
 * @param script The ipfs url of the script executed.
 * @param payload The bytes representing the output of the script.
 * @param requester The job requester.
 */
public data class FulfillCall(
    val script: ByteArray,
    val payload: ByteArray,
    val requester: MultiAddress,
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return CALL_INDEX +
                script.size.toLong().toCompactU8a() + script +
                payload.size.toLong().toCompactU8a() + payload +
                requester.toU8a()
    }
}
