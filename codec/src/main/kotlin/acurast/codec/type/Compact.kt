package acurast.codec.type

public data class Compact<T: ToCompactU8a>(val x: T): ToU8a {
    override fun toU8a(): ByteArray {
        return  x.toCompactU8a()
    }
}