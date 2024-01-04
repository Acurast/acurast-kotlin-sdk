package acurast.codec.type.contracts

import acurast.codec.extensions.*
import acurast.codec.type.*
import java.nio.ByteBuffer

public data class ContractApiCall(
    public val origin: ByteArray,
    public val destination: ByteArray,
    public val value: UInt128,
    public val gasLimit: Option<Weight>,
    public val storageDepositLimit: Option<UInt128>,
    public val data: ByteArray
): ToU8a {
    public companion object {
        public fun read(buffer: ByteBuffer): ContractApiCall {
            return ContractApiCall(
                origin = buffer.readAccountId32().toU8a(),
                destination = buffer.readAccountId32().toU8a(),
                value = UInt128(buffer.readU128()),
                gasLimit = Option.Decoder.read(buffer) { readWeight() },
                storageDepositLimit = Option.Decoder.read(buffer) { UInt128(readU128()) },
                data = buffer.readByteArray(),
            )
        }
    }

    override fun toU8a(): ByteArray {
        return origin + destination + value.toU8a() +
                gasLimit.toU8a() + storageDepositLimit.toU8a() +
                data.size.toLong().toCompactU8a() + data
    }
}