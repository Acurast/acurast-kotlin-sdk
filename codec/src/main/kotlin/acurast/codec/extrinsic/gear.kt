package acurast.codec.extrinsic

import acurast.codec.extensions.calculateHashCode
import acurast.codec.extensions.equals
import acurast.codec.extensions.toU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.UInt128
import acurast.codec.type.UInt64

public data class GearSendMessage(
    val callIndex: ByteArray,
    val destination: ByteArray,
    val payload: ByteArray,
    val gasLimit: UInt64,
    val value: UInt128,
    val keepAlive: Boolean,
) : ExtrinsicCall {
    override fun toU8a(): ByteArray =
        callIndex + destination + payload.toU8a() + gasLimit.toU8a() + value.toU8a() + keepAlive.toU8a()

    override fun equals(other: Any?): Boolean =
        equals(other) {
            callIndex.contentEquals(it.callIndex)
                    && destination.contentEquals(it.destination)
                    && payload.contentEquals(it.payload)
                    && gasLimit == it.gasLimit
                    && value == it.value
                    && keepAlive == it.keepAlive
        }

    override fun hashCode(): Int = listOf(
        callIndex.contentHashCode(),
        destination.contentHashCode(),
        payload.contentHashCode(),
        gasLimit.hashCode(),
        value.hashCode(),
        keepAlive.hashCode(),
    ).calculateHashCode()
}