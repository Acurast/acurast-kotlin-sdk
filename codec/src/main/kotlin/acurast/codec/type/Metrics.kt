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
    public val config: List<Config>,
    public val name: String,
    public val reward: Reward,
    public val total: Total,
) {
    public companion object {
        public fun read(bytes: ByteBuffer): MetricPool {
            val config = bytes.readList { Config.read(this) }
            val name = bytes.readByteArray(n = 24).toString(charset = Charsets.UTF_8)
            val reward = Reward.read(bytes)
            val total = Total.read(bytes)

            return MetricPool(config, name, reward, total)
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

    public data class Reward(public val current: BigDecimal, public val next: Next?) {
        public data class Next(val epoch: Long, val value: BigDecimal) {
            public companion object {
                public fun read(bytes: ByteBuffer): Next {
                    val epoch = bytes.readU32()
                    val value = bytes.readPerquintill()

                    return Next(epoch.toLong(), value)
                }
            }
        }

        public companion object {
            public fun read(bytes: ByteBuffer): Reward {
                val current = bytes.readPerquintill()
                val next = bytes.readOptional { Next.read(this) }

                return Reward(current, next)
            }
        }
    }

    public data class Total(public val epoch: Long, public val prev: BigDecimal, public val cur: BigDecimal) {
        public companion object {
            public fun read(bytes: ByteBuffer): Total {
                val epoch = bytes.readU32()
                val prev = bytes.readFixedU128()
                val cur = bytes.readFixedU128()

                return Total(epoch.toLong(), prev, cur)
            }
        }
    }
}
