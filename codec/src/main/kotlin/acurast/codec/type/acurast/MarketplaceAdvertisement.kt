package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.*
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer

/**
 * The structure of a Processor Advertisement in the marketplace.
 */
public data class MarketplaceAdvertisement(
    public val pricing: MarketplacePricing,
    public val restriction: MarketplaceAdvertisementRestriction,
) : ToU8a {
    override fun toU8a(): ByteArray {
        return pricing.toU8a() + restriction.toU8a()
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
public data class MarketplacePricing(
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

    public companion object {
        public fun read(buffer: ByteBuffer): MarketplacePricing {
            val feePerMillisecond = buffer.readU128()
            val feePerStorageByte = buffer.readU128()
            val baseFeePerExecution = buffer.readU128()
            val schedulingWindow = SchedulingWindow.read(buffer)

            return MarketplacePricing(
                UInt128(feePerMillisecond),
                UInt128(feePerStorageByte),
                UInt128(baseFeePerExecution),
                schedulingWindow,
            )
        }
    }
}

public data class MarketplaceAdvertisementRestriction(
    public val maxMemory: Int,
    public val networkRequestQuota: Byte,
    public val storageCapacity: Int,
    public val allowedConsumers: Option<List<MultiOrigin>>,
    public val availableModules: List<JobModule> = emptyList(),
) : ToU8a {
    override fun toU8a(): ByteArray {
        return maxMemory.toU8a() +
                networkRequestQuota.toU8a() +
                storageCapacity.toU8a() +
                allowedConsumers.toU8a() +
                availableModules.toU8a(withSize = true)
    }

    public companion object {
        public fun read(buffer: ByteBuffer): MarketplaceAdvertisementRestriction {
            val maxMemory = buffer.readU32()
            val networkRequestQuota = buffer.readU8()
            val storageCapacity = buffer.readU32()
            val allowedConsumers = buffer.readOptional { readList { MultiOrigin.read(this@readList) } }
            val availableModules = buffer.readList { JobModule.read(this) }

            return MarketplaceAdvertisementRestriction(
                maxMemory.toInt(),
                networkRequestQuota.toByte(),
                storageCapacity.toInt(),
                allowedConsumers?.let { Option.some(it) } ?: Option.none(),
                availableModules,
            )
        }
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

    public companion object {
        public fun read(buffer: ByteBuffer): SchedulingWindow {
            val kind = Kind.entries.first { it.id == buffer.readU8().toInt() }
            val time = buffer.readU64()

            return SchedulingWindow(kind, UInt64(time.toLong()))
        }
    }
}