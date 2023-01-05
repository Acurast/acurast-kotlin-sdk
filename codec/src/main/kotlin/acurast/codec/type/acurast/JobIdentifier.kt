package acurast.codec.type.acurast

import acurast.codec.type.AccountId32

/**
 * Job identifier structure
 */
public data class JobIdentifier(
    public val requester: AccountId32,
    public val script: ByteArray
)