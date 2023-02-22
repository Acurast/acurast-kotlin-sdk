package acurast.rpc.type

import acurast.codec.extensions.readU128
import acurast.codec.extensions.readU32
import java.math.BigInteger
import java.nio.ByteBuffer

public data class FrameSystemAccountInfo @OptIn(ExperimentalUnsignedTypes::class) constructor(
    val nonce: UInt = 0U,
    val consumers: UInt = 0U,
    val providers: UInt = 0U,
    val sufficients: UInt = 0U,
    val data: FrameSystemAccountInfoData = FrameSystemAccountInfoData()
) {
    public data class FrameSystemAccountInfoData constructor(
        var free: BigInteger = BigInteger.ZERO,
        var reserved: BigInteger = BigInteger.ZERO,
        var miscFrozen: BigInteger = BigInteger.ZERO,
        var feeFrozen: BigInteger = BigInteger.ZERO,
    )
}

@OptIn(ExperimentalUnsignedTypes::class)
public fun ByteBuffer.readAccountInfo(): FrameSystemAccountInfo {
    return FrameSystemAccountInfo(
        readU32(),
        readU32(),
        readU32(),
        readU32(),
        FrameSystemAccountInfo.FrameSystemAccountInfoData(
            readU128(),
            readU128(),
            readU128(),
            readU128(),
        )
    )
}