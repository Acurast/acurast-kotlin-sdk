package acurast.rpc.versioned

import acurast.rpc.versioned.v0.V0AcurastRpc

public typealias LatestAcurastRpc = V0AcurastRpc

public interface VersionedAcurastRpc : LatestAcurastRpc {
    public val version: UInt
}
