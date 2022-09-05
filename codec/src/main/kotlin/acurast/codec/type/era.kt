package acurast.codec.type

import kotlin.math.log2

/**
 * The mortal or immortal era of a given extrinsic.
 */
public interface ExtrinsicEra: ToU8a

public data class ImmortalEra(val value: Byte = 0x00) : ExtrinsicEra {
    override fun toU8a(): ByteArray {
        return byteArrayOf(value)
    }
}

public data class MortalEra(val period: Int, val phase: Int) : ExtrinsicEra {
    public companion object {
        /**
         * Build a mortal era.
         */
        @OptIn(ExperimentalUnsignedTypes::class)
        public fun from(era: UInt, _period: Int = 50): MortalEra {
            var period = StrictMath.pow(2.0, StrictMath.ceil(log2(_period.toDouble())));
            period = StrictMath.min(StrictMath.max(period, 4.0), (1 shl 16).toDouble());

            val phase = era % period.toUInt();

            val quantizeFactor = StrictMath.max((period.toInt() shr 12).toDouble(), 1.0);
            val quantizePhase = (phase / quantizeFactor.toUInt()) * quantizeFactor.toUInt();

            return MortalEra(period.toInt(), quantizePhase.toInt());
        }
    }
    override fun toU8a(): ByteArray {
        val quantizeFactor = (period shr 12).coerceAtLeast(1)
        val trailingZeros = java.lang.Long.numberOfTrailingZeros(period.toLong())
        val encoded =
                (trailingZeros - 1).coerceAtLeast(1).coerceAtMost(15) + (((phase / quantizeFactor) shl 4))
        val first = encoded shr 8
        val second = encoded and 0xff

        return byteArrayOf(second.toByte(), first.toByte())
    }
}