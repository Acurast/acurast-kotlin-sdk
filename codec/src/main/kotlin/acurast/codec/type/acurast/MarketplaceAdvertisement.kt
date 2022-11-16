package acurast.codec.type.acurast

import acurast.codec.extensions.toU8a
import acurast.codec.type.AccountId32
import acurast.codec.type.ToU8a
import acurast.codec.type.UInt128

/**
 * The structure of a Processor Advertisement in the marketplace.
 */
public data class MarketplaceAdvertisement(
    public val pricing: List<MarketplacePricing>,
    public val capacity: Int,
    public val allowedConsumers: List<AccountId32>? = null,
)

/**
 * The structure of the pricing accepted by the data processor.
 */
public data class MarketplacePricing constructor(
    public val rewardAsset: Int,
    public val pricePerCpuMillisecond: UInt128,
    public val bonus: UInt128,
    public val maximumSlash: UInt128,
): ToU8a {
    override fun toU8a(): ByteArray {
        return rewardAsset.toU8a() +
                pricePerCpuMillisecond.toU8a() +
                bonus.toU8a() +
                maximumSlash.toU8a()
    }
}