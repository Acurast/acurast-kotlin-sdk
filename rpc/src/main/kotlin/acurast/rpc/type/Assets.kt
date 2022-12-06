package acurast.rpc.type

import acurast.codec.extensions.readBoolean
import acurast.codec.extensions.readByte
import acurast.codec.extensions.readU128
import java.math.BigInteger
import java.nio.ByteBuffer

public data class PalletAssetsAssetAccount constructor(
    val balance: BigInteger,
    val isFrozen: Boolean,
    val reason: Byte,
)

public fun ByteBuffer.readPalletAssetsAssetAccount(): PalletAssetsAssetAccount {
    return PalletAssetsAssetAccount(
        readU128(),
        readBoolean(),
        readByte(),
    )
}