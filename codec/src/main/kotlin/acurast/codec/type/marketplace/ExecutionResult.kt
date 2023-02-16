package acurast.codec.type.marketplace

import acurast.codec.extensions.toU8a
import acurast.codec.type.ToU8a

/**
 * This structure represents the result of a job execution.
 */
public data class ExecutionResult(val kind: Kind, val payload: ByteArray): ToU8a {

    public enum class Kind(public val id: Byte): ToU8a {
        Success(0),
        Failure(1);

        override fun toU8a(): ByteArray = id.toU8a()
    }

    override fun toU8a(): ByteArray = kind.toU8a() + payload.toU8a()
}