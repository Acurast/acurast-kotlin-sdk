package acurast.rpc.versioned.v0

import acurast.rpc.versioned.VersionedAcurastRpc

internal interface V0VersionedCompatAcurastRpc : V0AcurastRpc, /* Forwards Compatibility */ VersionedAcurastRpc

private class V0VersionedCompatAcurastRpcImpl(v0: V0AcurastRpc) : V0VersionedCompatAcurastRpc, V0AcurastRpc by v0 {
    override val version: UInt = V0AcurastRpc.VERSION

    /* Forwards Compatibility */
}

internal fun V0AcurastRpc.compat(): V0VersionedCompatAcurastRpc = V0VersionedCompatAcurastRpcImpl(this)