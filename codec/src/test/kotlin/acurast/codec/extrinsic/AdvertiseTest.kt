package acurast.codec.extrinsic

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.extensions.toU8a
import acurast.codec.type.*
import acurast.codec.type.acurast.MarketplaceAdvertisement
import acurast.codec.type.acurast.MarketplacePricing
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class AdvertiseTest {
    @Test
    fun encodeAdvertiseCall() {
        val advertisement = MarketplaceAdvertisement(
            pricing = listOf(
                MarketplacePricing(
                    rewardAsset = 0,
                    pricePerCpuMillisecond = UInt128(BigInteger.ONE),
                    bonus = UInt128(BigInteger.ONE),
                    maximumSlash = UInt128(BigInteger.ONE),
                )
            ),
            capacity = 10,
            allowedConsumers = listOf(AccountId32("d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa()))
        )
        val call = AdvertiseCall(advertisement)
        Assert.assertEquals(call.toU8a().toHex(), "2b0004000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000a0000000104d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
    }

    @Test
    fun encodeFulfillExtrinsic() {
        val requester = "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa();
        val advertisement = MarketplaceAdvertisement(
            pricing = listOf(
                MarketplacePricing(
                    rewardAsset = 0,
                    pricePerCpuMillisecond = UInt128(BigInteger.ONE),
                    bonus = UInt128(BigInteger.ONE),
                    maximumSlash = UInt128(BigInteger.ONE),
                )
            ),
            capacity = 10,
            allowedConsumers = listOf(AccountId32("d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa()))
        )
        val call = AdvertiseCall(advertisement)

        val extrinsic = Extrinsic(
            ExtrinsicSignature(
                MultiAddress(AccountIdentifier.AccountID, requester),
                MultiSignature(CurveKind.Sr25519, "b679f84970170d9c5ea375825907f7df7bd76656abd1311bbfebdadbfdd459215052305c7bc3a5b7afde7ae4b34cbbbdfaa842a6183f3d1e5d257bcb9581ae80".hexToBa()),
                MortalEra(64, 55),
                0,
                BigInteger.ZERO,
            ),
            call
        )

        Assert.assertEquals(
            extrinsic.toU8a().toHex(),
            "11038400d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d01b679f84970170d9c5ea375825907f7df7bd76656abd1311bbfebdadbfdd459215052305c7bc3a5b7afde7ae4b34cbbbdfaa842a6183f3d1e5d257bcb9581ae80750300002b0004000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000a0000000104d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
        )
   }
}