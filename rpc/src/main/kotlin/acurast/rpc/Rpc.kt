package acurast.rpc

import acurast.rpc.engine.RpcEngine

public interface Rpc {
    public val defaultEngine: RpcEngine<*>
}

internal object JsonRpc {
    object Key {
        const val RESULT = "result"
        const val ERROR = "error"
        const val MESSAGE = "message"
    }
}