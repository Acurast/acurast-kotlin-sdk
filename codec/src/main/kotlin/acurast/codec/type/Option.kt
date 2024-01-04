package acurast.codec.type

import acurast.codec.extensions.littleEndian
import acurast.codec.extensions.readByte
import acurast.codec.extensions.toU8a
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer

public sealed interface Option<T> : ToU8a {
    public enum class Tag(public val id: Byte): ToU8a {
        None(0),
        Some(1);

        override fun toU8a(): ByteArray = id.toU8a()
    }

    public sealed interface Kind {
        public val tag: Tag

        public companion object {
            internal val values: List<Kind>
                get() = listOf(
                    None,
                    Some
                )
        }
    }

    public object Decoder {
        public inline fun <reified T> read(buffer: ByteBuffer, optionalParser: ByteBuffer.() -> T): Option<T> = buffer.littleEndian {
            return when (val tag = buffer.readByte()) {
                Tag.None.id -> None()
                Tag.Some.id -> Some(optionalParser(buffer))
                else -> throw UnsupportedEncodingException("Unknown option tag: $tag")
            }
        }
    }

    public class None<T> : Option<T>  {
        public companion object : Kind {
            override val tag: Tag = Tag.None
        }

        override fun toU8a(): ByteArray = tag.toU8a()
    }

    public data class Some<T>(private val inner: T) : Option<T>  {
        public companion object : Kind {
            override val tag: Tag = Tag.Some
        }

        override fun toU8a(): ByteArray = tag.toU8a() + when (inner) {
            is ByteArray -> inner.toU8a()
            is ToU8a -> inner.toU8a()
            is List<*> -> tryCast<List<ToU8a>>(inner).toU8a()
            else -> throw UnsupportedEncodingException()
        }
    }

    public companion object {
        public fun <T> some(item: T): Option<T> = Some(item)
        public fun <T> none(): Option<T> = None()
    }
}

public inline fun <reified T> tryCast(instance: Any?): T {
    return if (instance is T) instance else throw UnsupportedEncodingException()
}