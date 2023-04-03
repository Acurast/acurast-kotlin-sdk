package acurast.codec.extrinsic

import acurast.codec.extensions.toCompactU8a
import acurast.codec.type.*

public data class ContractCall(
    val callIndex: ByteArray,
    val destination: MultiAddress,
    val value: Compact<UInt128>,
    val refTime: Compact<UInt64>,
    val proofSize: Compact<UInt64>,
    val storageDepositLimit: Option<Compact<UInt128>>,
    val data: ByteArray
) : ExtrinsicCall {
    override fun toU8a(): ByteArray {
        return callIndex + destination.toU8a() + value.toU8a() + refTime.toU8a() + proofSize.toU8a() + storageDepositLimit.toU8a() + data.size.toLong()
            .toCompactU8a() + data
    }
}