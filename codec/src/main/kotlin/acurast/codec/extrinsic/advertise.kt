package acurast.codec.extrinsic

import acurast.codec.extensions.toCompactU8a
import acurast.codec.extensions.toU8a
import acurast.codec.type.ExtrinsicCall
import acurast.codec.type.acurast.MarketplaceAdvertisement

private val CALL_INDEX: ByteArray = byteArrayOf(0x2b, 0x00);

/**
 * Extrinsic method call 'advertise'.
 *
 * @param advertisement The advertisement information of the data processor.
 */
public data class AdvertiseCall(val advertisement: MarketplaceAdvertisement): ExtrinsicCall {
    override fun toU8a(): ByteArray {
        val bytes = CALL_INDEX +
                advertisement.pricing.size.toLong().toCompactU8a() +
                advertisement.pricing.fold(byteArrayOf()) { acc, item -> acc + item.toU8a() } +
                advertisement.capacity.toU8a();

        if (advertisement.allowedConsumers != null) {
            return bytes + byteArrayOf(1) /* Means "Some" */ +
                advertisement.allowedConsumers.size.toLong().toCompactU8a() +
                advertisement.allowedConsumers.fold(byteArrayOf()) { acc, account ->
                    acc + account.toU8a()
                }
        }

        return bytes + byteArrayOf(0)
    }
}