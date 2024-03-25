package acurast.rpc.versioned.storage.v1

import acurast.rpc.engine.RpcEngine
import acurast.rpc.pallet.State
import acurast.rpc.versioned.storage.v0.V0AcurastStorage
import acurast.rpc.versioned.storage.v0.V0AcurastStorageImpl

public interface V1AcurastStorage : V0AcurastStorage {
    public companion object {
        public const val VERSION: UInt = 1u
    }
}

internal fun V1AcurastStorage(engine: RpcEngine, state: State): V1AcurastStorage = V1AcurastStorageImpl(engine, state)

private class V1AcurastStorageImpl(engine: RpcEngine, state: State) : V1AcurastStorage , V0AcurastStorage by V0AcurastStorage(engine, state) {
    override val version: UInt = V1AcurastStorage.VERSION
}