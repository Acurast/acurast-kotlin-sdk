package acurast.codec.type

import acurast.codec.extensions.*
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer

public data class Metrics(
    val poolId: Byte,
    val numerator: UInt128,
    val denominator: UInt128,
) : ToU8a {
    override fun toU8a(): ByteArray =
        poolId.toU8a() + numerator.toU8a() + denominator.toU8a()

    public companion object
}

public fun Metrics(poolId: Byte, numerator: BigInteger, denominator: BigInteger): Metrics =
    Metrics(poolId, UInt128(numerator), UInt128(denominator))

public fun Metrics(poolId: Byte, value: Long): Metrics =
    Metrics(poolId, BigInteger.valueOf(value), BigInteger.ONE)

public fun Metrics(poolId: Byte, value: Double): Metrics {
    val value = BigDecimal(value.toString())
    val numerator = value.unscaledValue()
    val denominator = BigInteger.TEN.pow(value.scale())

    return Metrics(poolId, numerator, denominator)
}

public data class MetricPool(
    public val config: List<Config>
) {
    public companion object {
        public fun read(bytes: ByteBuffer): MetricPool {
            val config = bytes.readList { Config.read(this) }

            return MetricPool(config)
        }
    }

    public data class Config(public val name: String, public val numerator: BigInteger, public val denominator: BigInteger) {
        public companion object {
            public fun read(bytes: ByteBuffer): Config {
                val name = bytes.readBytes(24).decodeToString()
                val numerator = bytes.readU128()
                val denominator = bytes.readU128()

                return Config(name, numerator, denominator)
            }
        }
    }
}
