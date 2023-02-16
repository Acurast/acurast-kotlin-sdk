package acurast.codec.type.acurast

import acurast.codec.extensions.toU8a
import acurast.codec.type.*

/**
 * The structure of a Processor Advertisement in the marketplace.
 */
public data class MarketplaceAdvertisement(
    public val pricing: List<MarketplacePricing>,
    public val maxMemory: Int,
    public val networkRequestQuota: Byte,
    public val storageCapacity: Int,
    public val allowedConsumers: Option<List<AccountId32>>,
): ToU8a {
    override fun toU8a(): ByteArray {
        return pricing.toU8a(withSize = true) +
                maxMemory.toU8a() +
                networkRequestQuota.toU8a() +
                storageCapacity.toU8a() +
                allowedConsumers.toU8a()

    }
}

/**
 * The structure of the pricing accepted by the data processor.
 */
public data class MarketplacePricing constructor(
    public val rewardAsset: AssetId,
    public val feePerMillisecond: UInt128,
    public val feePerStorageByte: UInt128,
    public val baseFeePerExecution: UInt128,
    public val schedulingWindow: SchedulingWindow,
): ToU8a {
    override fun toU8a(): ByteArray {
        return rewardAsset.toU8a() +
                feePerMillisecond.toU8a() +
                feePerStorageByte.toU8a() +
                baseFeePerExecution.toU8a() +
                schedulingWindow.toU8a()
    }
}

public enum class SchedulingWindowKind(public val id : Int) {
    End(0),
    Delta(1)
}

public data class SchedulingWindow(public val kind : Kind, public val time: UInt64): ToU8a {
    public enum class Kind(public val id : Int) {
        End(0),
        Delta(1)
    }

    override fun toU8a(): ByteArray {
        return kind.id.toByte().toU8a() + time.toU8a()
    }
}