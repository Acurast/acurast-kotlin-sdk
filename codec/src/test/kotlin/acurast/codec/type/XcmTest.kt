package acurast.codec.type

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.nio.ByteBuffer

class XcmTest {
    @Test
    fun encodeMultiLocation() {
        val location = MultiLocation(
            parents = 1,
            interior = JunctionsV1.X3(
                JunctionV1.Parachain(1000),
                JunctionV1.PalletInstance(50),
                JunctionV1.GeneralIndex(BigInteger.valueOf(22)),
            )
        )

        val expectedEncoding = "010300a10f04320558"
        Assert.assertEquals(expectedEncoding, location.toU8a().toHex())

        val decoded = MultiLocation.read(ByteBuffer.wrap(expectedEncoding.hexToBa()))
        Assert.assertEquals(decoded, location)
    }

    @Test
    fun encodeAssetId() {
        val assetId = AssetId(MultiLocation(
            parents = 1,
            interior = JunctionsV1.X3(
                JunctionV1.Parachain(1000),
                JunctionV1.PalletInstance(50),
                JunctionV1.GeneralIndex(BigInteger.valueOf(22)),
            )
        ))

        val expectedEncoding = "00010300a10f04320558"
        Assert.assertEquals(expectedEncoding, assetId.toU8a().toHex())

        val decoded = AssetId.read(ByteBuffer.wrap(expectedEncoding.hexToBa()))
        Assert.assertEquals(decoded, assetId)
    }
}
