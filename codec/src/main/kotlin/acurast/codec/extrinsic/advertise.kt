package acurast.codec.extrinsic

import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.acurast.MarketplaceAdvertisement

/**
 * Extrinsic method call 'advertise'.
 *
 * @param advertisement The advertisement information of the data processor.
 */
public data class AdvertiseCall(
    val callIndex: ByteArray,
    val advertisement: MarketplaceAdvertisement
): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex + advertisement.toU8a()
    }
}