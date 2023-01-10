package acurast.codec.extrinsic

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.*
import acurast.codec.type.acurast.MarketplaceAdvertisement
import acurast.codec.type.acurast.MarketplacePricing
import acurast.codec.type.acurast.SchedulingWindow
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class AdvertiseTest {
    @Test
    fun encodeAdvertiseCall() {
        val advertisement = MarketplaceAdvertisement(
            pricing = listOf(
                MarketplacePricing(
                    rewardAsset = 22,
                    feePerMillisecond = UInt128(BigInteger.ONE),
                    feePerStorageByte = UInt128(BigInteger.ONE),
                    baseFeePerExecution = UInt128(BigInteger.ONE),
                    schedulingWindow = SchedulingWindow(SchedulingWindow.Kind.End, UInt64(10000))
                )
            ),
            maxMemory = 1,
            networkRequestQuota = 2,
            storageCapacity = 3,
            allowedConsumers = listOf(AccountId32("d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa()))
        )
        val call = AdvertiseCall(byteArrayOf(0x2b, 0x00), advertisement)
        Assert.assertEquals(call.toU8a().toHex(), "2b0004160000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000010270000000000000100000002030000000104d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
    }
}