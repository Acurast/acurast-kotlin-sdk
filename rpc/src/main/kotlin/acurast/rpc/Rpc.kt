package acurast.rpc

import acurast.rpc.engine.RpcEngine

public interface Rpc {
    public val defaultEngine: RpcEngine<*>
}