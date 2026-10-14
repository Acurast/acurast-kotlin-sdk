package acurast.codec.type

import acurast.codec.extensions.ScaleDecoderException
import acurast.codec.extensions.readU8
import java.nio.ByteBuffer
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
    /** The first block in which a transaction with this era is valid, if built at [currentBlock]. */
    public fun birthBlock(currentBlock: Long): Long = (maxOf(currentBlock, phase.toLong()) - phase) / period * period + phase

    /** The first block in which a transaction with this era is no longer valid, if built at [currentBlock]. */
    public fun deathBlock(currentBlock: Long): Long = birthBlock(currentBlock) + period

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

/** Decodes an encoded era (one byte for immortal, two for mortal). */
public fun ByteBuffer.readExtrinsicEra(): ExtrinsicEra {
    val first = readU8().toInt()
    if (first == 0) return ImmortalEra()

    val encoded = first or (readU8().toInt() shl 8)
    val period = 2 shl (encoded % (1 shl 4))
    val quantizeFactor = (period shr 12).coerceAtLeast(1)
    val phase = (encoded shr 4) * quantizeFactor
    if (period < 4 || phase >= period) throw ScaleDecoderException("Invalid mortal era")

    return MortalEra(period, phase)
}
