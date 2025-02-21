package acurast.codec.type.manager

import acurast.codec.type.ToU8a
import acurast.codec.type.UInt128
import java.math.BigDecimal
import java.math.BigInteger

public data class Metrics(
    val poolName: String, // 8 bytes
    val numerator: UInt128,
    val denominator: UInt128,
) : ToU8a {
    override fun toU8a(): ByteArray =
        poolName.toByteArray(charset = Charsets.UTF_8).copyOf(8) + numerator.toU8a() + denominator.toU8a()

    public companion object
}

public fun Metrics(poolName: String, numerator: BigInteger, denominator: BigInteger): Metrics =
    Metrics(poolName, UInt128(numerator), UInt128(denominator))

public fun Metrics(poolName: String, value: Long): Metrics =
    Metrics(poolName, BigInteger.valueOf(value), BigInteger.ONE)

public fun Metrics(poolName: String, value: Double): Metrics {
    val value = BigDecimal(value.toString())
    val numerator = value.unscaledValue()
    val denominator = BigInteger.TEN.pow(value.scale())

    return Metrics(poolName, numerator, denominator)
}