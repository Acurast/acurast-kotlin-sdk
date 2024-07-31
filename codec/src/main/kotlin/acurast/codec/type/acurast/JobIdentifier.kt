package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.AccountId32
import acurast.codec.type.ToU8a
import acurast.codec.type.readAccountId32
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.nio.ByteBuffer

/**
 * Identifies the origin chain and address
 */
public data class MultiOrigin(
    public val kind: Kind,
    public val source: ByteArray
): ToU8a {
    public companion object {
        public fun Acurast(address: AccountId32): MultiOrigin = MultiOrigin(Kind.Acurast, address.toU8a())
        public fun Tezos(payload: ByteArray): MultiOrigin = MultiOrigin(Kind.Tezos, payload.toU8a())
        public fun AlephZero(address: AccountId32): MultiOrigin = MultiOrigin(Kind.AlephZero, address.toU8a())
        public fun Vara(address: AccountId32): MultiOrigin = MultiOrigin(Kind.Vara, address.toU8a())

        public fun read(buffer: ByteBuffer): MultiOrigin {
            return when (val kind = buffer.readByte()) {
                Kind.Acurast.id -> Acurast(buffer.readAccountId32())
                Kind.Tezos.id -> Tezos(buffer.readByteArray())
                Kind.AlephZero.id -> AlephZero(buffer.readAccountId32())
                Kind.Vara.id -> Vara(buffer.readAccountId32())
                else -> throw UnsupportedEncodingException("Unknown multi origin kind $kind.")
            }
        }
    }
    public enum class Kind(public val id: Byte): ToU8a {
        Acurast(0),
        Tezos(1),
        // Ethereum(2),
        AlephZero(3),
        Vara(4);

        public override fun toU8a(): ByteArray = id.toU8a()

        public override fun toString(): String = when (this) {
            Acurast -> "Acurast"
            Tezos -> "Tezos"
            AlephZero -> "AlephZero"
            Vara -> "Vara"
        }
    }

    public override fun toU8a(): ByteArray {
        return kind.toU8a() + source
    }

    public override fun toString(): String = "(kind: ${kind}, source: ${source.toHex()})"

    override fun equals(other: Any?): Boolean = equals(other) { kind == it.kind && source.contentEquals(it.source) }
    override fun hashCode(): Int = listOf(kind.hashCode(), source.contentHashCode()).calculateHashCode()
}

/**
 * Job identifier structure
 */
public data class JobIdentifier(
    public val origin: MultiOrigin,
    public val id: BigInteger
): ToU8a {
    public companion object {
        public fun read(buffer: ByteBuffer): JobIdentifier {
            val origin = MultiOrigin.read(buffer)
            val id = buffer.readU128()

            return JobIdentifier(origin, id)
        }
    }

    public override fun toU8a(): ByteArray = origin.toU8a() + id.toU8a()

    public override fun toString(): String = "(origin: ${origin}, id: ${id.toString(10)})"
}