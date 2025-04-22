package acurast.rpc.utils

import acurast.codec.extensions.hexToBa
import java.nio.ByteBuffer

internal fun <T> List<List<String>>.readChangeValueOrNull(index: Int, read: (ByteBuffer) -> T): T? =
    getOrNull(index)?.readChangeValueOrNull(read)

internal fun <T> List<String>.readChangeValueOrNull(read: (ByteBuffer) -> T): T? =
    getOrNull(1)
        ?.takeIf { it.isNotEmpty() }
        ?.let { read(ByteBuffer.wrap(it.hexToBa())) }