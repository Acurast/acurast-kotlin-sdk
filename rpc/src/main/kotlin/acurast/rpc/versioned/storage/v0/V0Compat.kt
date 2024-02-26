package acurast.rpc.versioned.storage.v0

import acurast.rpc.versioned.storage.AcurastStorage

internal interface V0CompatAcurastStorage : V0AcurastStorage, /* Forwards Compatibility */ AcurastStorage

private class V0CompatAcurastStorageImpl(v0: V0AcurastStorage) : V0CompatAcurastStorage, V0AcurastStorage by v0 {
    /* Forwards Compatibility */
}

internal fun V0AcurastStorage.compat(): V0CompatAcurastStorage = V0CompatAcurastStorageImpl(this)