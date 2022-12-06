package acurast.rpc.type

import acurast.codec.extensions.readU32
import java.nio.ByteBuffer

public data class PalletAssetsAssetAccount @OptIn(ExperimentalUnsignedTypes::class) constructor(
    var balance: UInt,
)

@OptIn(ExperimentalUnsignedTypes::class)
public fun ByteBuffer.readPalletAssetsAssetAccount(): PalletAssetsAssetAccount {
    return PalletAssetsAssetAccount(
        readU32(),
    )
}