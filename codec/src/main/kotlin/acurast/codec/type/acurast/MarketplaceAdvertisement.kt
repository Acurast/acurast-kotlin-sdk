package acurast.codec.type.acurast

import acurast.codec.extensions.toCompactU8a
import acurast.codec.extensions.toU8a
import acurast.codec.type.AccountId32
import acurast.codec.type.ToU8a
import acurast.codec.type.UInt128
import acurast.codec.type.UInt64

/**
 * The structure of a Processor Advertisement in the marketplace.
 */
public data class MarketplaceAdvertisement(
    public val pricing: List<MarketplacePricing>,
    public val maxMemory: Int,
    public val networkRequestQuota: Byte,
    public val storageCapacity: Int,
    public val allowedConsumers: List<AccountId32>? = null,
): ToU8a {
    override fun toU8a(): ByteArray {
        val bytes = pricing.size.toLong().toCompactU8a() +
                pricing.fold(byteArrayOf()) { acc, item -> acc + item.toU8a() } +
                maxMemory.toU8a() +
                networkRequestQuota.toU8a() +
                storageCapacity.toU8a()

        val allowedConsumersBytes = if (allowedConsumers != null) {
            byteArrayOf(1) /* Means "Some" */ +
                    allowedConsumers.size.toLong().toCompactU8a() +
                    allowedConsumers.fold(byteArrayOf()) { acc, account ->
                        acc + account.toU8a()
                    }
        } else {
            // Means "None"
            byteArrayOf(0)
        }

        return bytes + allowedConsumersBytes
    }
}

/**
 * The structure of the pricing accepted by the data processor.
 */
public data class MarketplacePricing constructor(
    public val rewardAsset: Int,
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