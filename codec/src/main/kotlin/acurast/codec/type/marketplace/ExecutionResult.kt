package acurast.codec.type.marketplace

import acurast.codec.extensions.toU8a
import acurast.codec.type.ToU8a

/**
 * This structure represents the result of a job execution.
 */
public class ExecutionResult private constructor(public val kind: Kind, public val payload: ByteArray): ToU8a {
    public companion object {
        public fun success(payload: ByteArray): ExecutionResult = ExecutionResult(Kind.Success, payload)
        public fun failure(payload: ByteArray): ExecutionResult = ExecutionResult(Kind.Failure, payload)
    }

    public enum class Kind(private val id: Byte): ToU8a {
        Success(0),
        Failure(1);
        override fun toU8a(): ByteArray = id.toU8a()
    }

    override fun toU8a(): ByteArray = kind.toU8a() + payload.toU8a()
}