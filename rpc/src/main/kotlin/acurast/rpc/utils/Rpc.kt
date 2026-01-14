package acurast.rpc.utils

import acurast.codec.extensions.hexToBa
import java.nio.ByteBuffer

internal fun <T> List<List<String>>.readChangeValueOrNull(index: Int, read: (ByteBuffer) -> T): T? =
    getOrNull(index)?.readChangeValueOrNull(read)

internal fun <T> List<String>.readChangeKeyOrNull(read: (ByteBuffer) -> T): T? = readChangeEntryOrNull(0, read)
internal fun <T> List<String>.readChangeValueOrNull(read: (ByteBuffer) -> T): T? = readChangeEntryOrNull(1, read)
private fun <T> List<String>.readChangeEntryOrNull(index: Int, read: (ByteBuffer) -> T): T? =
    getOrNull(index)
        ?.takeIf { it.isNotEmpty() }
        ?.let { read(ByteBuffer.wrap(it.hexToBa())) }