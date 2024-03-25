package acurast.rpc.versioned.storage.v1

import acurast.rpc.versioned.storage.AcurastStorage

internal interface V1CompatAcurastStorage : V1AcurastStorage, /* Forwards Compatibility */ AcurastStorage

private class V1CompatAcurastStorageImpl(v1: V1AcurastStorage) : V1CompatAcurastStorage, V1AcurastStorage by v1 {
    /* Forwards Compatibility */
}

internal fun V1AcurastStorage.compat(): V1CompatAcurastStorage = V1CompatAcurastStorageImpl(this)