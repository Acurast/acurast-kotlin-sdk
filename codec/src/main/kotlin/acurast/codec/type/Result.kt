package acurast.codec.type

import acurast.codec.extensions.littleEndian
import acurast.codec.extensions.readByte
import acurast.codec.extensions.toU8a
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer

public sealed interface Result<O, E> : ToU8a {
    public enum class Tag(public val id: Byte): ToU8a {
        Ok(0),
        Err(1);

        override fun toU8a(): ByteArray = id.toU8a()
    }

    public sealed interface Kind {
        public val tag: Tag

        public companion object {
            internal val values: List<Kind>
                get() = listOf(
                    Ok,
                    Err
                )
        }
    }

    public object Decoder {
        public inline fun <reified O, reified E> read(buffer: ByteBuffer, okParser: (b: ByteBuffer) -> O, errParser: (b: ByteBuffer) -> E): Result<O, E> = buffer.littleEndian {
            return when (val tag = buffer.readByte()) {
                Tag.Ok.id -> Ok(okParser(buffer))
                Tag.Err.id -> Err(errParser(buffer))
                else -> throw UnsupportedEncodingException("Unknown Result tag: $tag")
            }
        }
    }

    public data class Ok<O, E>(public val inner: O) : Result<O, E>  {
        public companion object : Kind {
            override val tag: Tag = Tag.Ok
        }

        override fun toU8a(): ByteArray = tag.toU8a() + when (inner) {
            is ByteArray -> inner.toU8a()
            is ToU8a -> inner.toU8a()
            is List<*> -> tryCast<List<ToU8a>>(inner).toU8a()
            else -> throw UnsupportedEncodingException("$inner")
        }
    }

    public data class Err<O, E>(public val inner: E) : Result<O, E>  {
        public companion object : Kind {
            override val tag: Tag = Tag.Err
        }

        override fun toU8a(): ByteArray = tag.toU8a() + when (inner) {
            is ByteArray -> inner.toU8a()
            is ToU8a -> inner.toU8a()
            is List<*> -> tryCast<List<ToU8a>>(inner).toU8a()
            else -> throw UnsupportedEncodingException()
        }
    }

    public companion object {
        public fun <O, E> ok(item: O): Result<O, E> = Ok(item)
        public fun <O, E> err(item: E): Result<O, E> = Err(item)
    }
}