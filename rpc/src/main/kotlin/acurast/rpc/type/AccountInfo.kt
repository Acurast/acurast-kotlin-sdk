package acurast.rpc.type

import acurast.codec.extensions.readU32
import java.nio.ByteBuffer

public data class AccountInfo @OptIn(ExperimentalUnsignedTypes::class) constructor(
    var nonce: UInt,
    var consumers: UInt,
    var providers: UInt,
)

@OptIn(ExperimentalUnsignedTypes::class)
public fun ByteBuffer.readAccountInfo(): AccountInfo {
    return AccountInfo(
        readU32(),
        readU32(),
        readU32(),
    )
}