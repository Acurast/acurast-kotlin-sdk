package acurast.rpc.type

import java.math.BigInteger

public data class Header(
    val parentHash: String,
    val number: BigInteger,
    val stateRoot: String,
    val extrinsicsRoot: String,
    // TODO: add digest field
)