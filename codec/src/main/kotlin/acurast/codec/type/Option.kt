package acurast.codec.type

import acurast.codec.extensions.toU8a
import java.io.UnsupportedEncodingException

public class Option<T> private constructor(private val item: T?) : ToU8a {
    public companion object {
        public fun <T> some(item: T): Option<T> = Option(item)
        public fun <T> none(): Option<T> = Option(null)
    }

    override fun toU8a(): ByteArray {
        return if (item == null) {
            // 0x00 Means "None"
            byteArrayOf(0)
        } else {
            //  0x01 Means "Some"
            byteArrayOf(1) + when (item) {
                is ByteArray -> item.toU8a()
                is List<*> -> tryCast<List<ToU8a>>(item).toU8a()
                is ToU8a -> item.toU8a()
                else -> throw UnsupportedEncodingException()
            }
        }
    }
}

public inline fun <reified T> tryCast(instance: Any?): T {
    return if (instance is T) instance else throw UnsupportedEncodingException()
}