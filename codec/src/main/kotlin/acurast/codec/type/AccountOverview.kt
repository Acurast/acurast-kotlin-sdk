package acurast.codec.type

import acurast.codec.type.compute.Commitment
import acurast.codec.type.compute.Delegation

public data class AccountOverview(
    val accountInfo: AccountInfo,
    val commitment: Commitment? = null,
    val delegations: List<Pair<Delegation, Commitment>> = emptyList(),
)