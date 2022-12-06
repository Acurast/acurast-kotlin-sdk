package acurast.rpc.type

import acurast.codec.extensions.*
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.nio.ByteBuffer

class AssetsTest {
    @Test
    fun readPalletAssetsAssetAccount() {
        val accountAsset = ByteBuffer.wrap("0x0027b9290000000000000000000000000000".hexToBa()).readPalletAssetsAssetAccount()
        Assert.assertEquals(accountAsset.balance, BigInteger("700000000"))
    }
}