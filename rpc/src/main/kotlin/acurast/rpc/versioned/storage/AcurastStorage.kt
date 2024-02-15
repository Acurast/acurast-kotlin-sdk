package acurast.rpc.versioned.storage

import acurast.rpc.versioned.storage.v0.V0AcurastStorage

public typealias LatestAcurastStorage = V0AcurastStorage

public interface AcurastStorage : VersionedAcurastStorage, LatestAcurastStorage
