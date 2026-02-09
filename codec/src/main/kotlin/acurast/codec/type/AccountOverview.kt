package acurast.codec.type

import acurast.codec.type.compute.Commitment
import acurast.codec.type.compute.Delegation
import acurast.codec.type.tokenconversion.TokenConversion
import acurast.codec.type.vesting.Vesting

public data class AccountOverview(
    val accountInfo: AccountInfo,
    val commitment: Commitment? = null,
    val delegations: List<Pair<Delegation, Commitment>> = emptyList(),
    val vesting: List<Vesting> = emptyList(),
    val conversion: TokenConversion? = null,
)