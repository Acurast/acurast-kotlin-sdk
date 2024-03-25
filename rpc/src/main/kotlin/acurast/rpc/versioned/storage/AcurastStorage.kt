package acurast.rpc.versioned.storage

import acurast.rpc.versioned.storage.v1.V1AcurastStorage

public typealias LatestAcurastStorage = V1AcurastStorage

public interface AcurastStorage : VersionedAcurastStorage, LatestAcurastStorage
