package acurast.codec.type.manager

import acurast.codec.extensions.toU8a
import acurast.codec.type.ToU8a
import acurast.codec.type.UInt128
import java.math.BigDecimal
import java.math.BigInteger

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