package acurast.rpc.type

import acurast.codec.extensions.readU128
import acurast.codec.extensions.readU32
import java.math.BigInteger
import java.nio.ByteBuffer

public data class FrameSystemAccountInfoData constructor(
    var free: BigInteger,
    var reserved: BigInteger,
    var miscFrozen: BigInteger,
    var feeFrozen: BigInteger
)

public data class FrameSystemAccountInfo @OptIn(ExperimentalUnsignedTypes::class) constructor(
    val nonce: UInt,
    val consumers: UInt,
    val providers: UInt,
    val sufficients: UInt,
    val data: FrameSystemAccountInfoData
)

@OptIn(ExperimentalUnsignedTypes::class)
public fun ByteBuffer.readAccountInfo(): FrameSystemAccountInfo {
    return FrameSystemAccountInfo(
        readU32(),
        readU32(),
        readU32(),
        readU32(),
        FrameSystemAccountInfoData(
            readU128(),
            readU128(),
            readU128(),
            readU128(),
        )
    )
}