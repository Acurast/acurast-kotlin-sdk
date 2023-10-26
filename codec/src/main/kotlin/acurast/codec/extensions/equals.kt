package acurast.codec.extensions

internal inline fun <reified T : Any> T.equals(other: Any?, compareProperties: T.(other: T) -> Boolean): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass || other !is T) return false

    return compareProperties(other)
}

internal fun List<Int>.calculateHashCode(): Int = fold(0) { acc, hash -> 31 * acc + hash }