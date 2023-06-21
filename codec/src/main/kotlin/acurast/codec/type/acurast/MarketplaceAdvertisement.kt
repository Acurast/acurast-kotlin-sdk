package acurast.codec.type.acurast

import acurast.codec.extensions.readByte
import acurast.codec.extensions.toU8a
import acurast.codec.type.*
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer

/**
 * The structure of a Processor Advertisement in the marketplace.
 */
public data class MarketplaceAdvertisement(
    public val pricing: MarketplacePricing,
    public val maxMemory: Int,
    public val networkRequestQuota: Byte,
    public val storageCapacity: Int,
    public val allowedConsumers: Option<List<MultiOrigin>>,
    public val availableModules: List<JobModule> = emptyList(),
) : ToU8a {
    override fun toU8a(): ByteArray {
        return pricing.toU8a() +
                maxMemory.toU8a() +
                networkRequestQuota.toU8a() +
                storageCapacity.toU8a() +
                allowedConsumers.toU8a() +
                availableModules.toU8a(withSize = true)

    }
}

/**
 * A module feature optionally supported by processors.
 */
public enum class JobModule(public val id: Byte) : ToU8a {
    DataEncryption(0)
    ;

    override fun toU8a(): ByteArray = this.id.toU8a()

    public companion object {
        public fun read(buffer: ByteBuffer): JobModule {
            return when (val id = buffer.readByte()) {
                DataEncryption.id -> DataEncryption
                else -> throw UnsupportedEncodingException("Unknown JobModule $id.")
            }
        }
    }
}

/**
 * The structure of the pricing accepted by the data processor.
 */
public data class MarketplacePricing constructor(
    public val feePerMillisecond: UInt128,
    public val feePerStorageByte: UInt128,
    public val baseFeePerExecution: UInt128,
    public val schedulingWindow: SchedulingWindow,
) : ToU8a {
    override fun toU8a(): ByteArray {
        return feePerMillisecond.toU8a() +
                feePerStorageByte.toU8a() +
                baseFeePerExecution.toU8a() +
                schedulingWindow.toU8a()
    }
}

public enum class SchedulingWindowKind(public val id: Int) {
    End(0),
    Delta(1)
}

public data class SchedulingWindow(public val kind: Kind, public val time: UInt64) : ToU8a {
    public enum class Kind(public val id: Int) {
        End(0),
        Delta(1)
    }

    override fun toU8a(): ByteArray {
        return kind.id.toByte().toU8a() + time.toU8a()
    }
}