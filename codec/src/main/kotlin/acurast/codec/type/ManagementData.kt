package acurast.codec.type

import acurast.codec.type.manager.ProcessorUpdateInfo

public data class ManagementData(
    val updateInfo: ProcessorUpdateInfo? = null,
    val managementEndpoint: String? = null,
)
