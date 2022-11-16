package acurast.rpc.type

public data class StorageQueryResult constructor(
    var block: String,
    var changes: List<List<String>>
)