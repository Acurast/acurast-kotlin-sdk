package acurast.codec.type

import java.math.BigInteger

public data class ProcessorOverview(
    val managerIdentifier: Int? = null,
    val isAttested: Boolean = false,
    val lastHeartbeatTimestamp: BigInteger? = null,
)
