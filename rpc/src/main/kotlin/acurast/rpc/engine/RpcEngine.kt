package acurast.rpc.engine

import acurast.rpc.utils.jsonRpcRequest
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

public interface RpcEngine<E : RpcEngine.Executor> {

    public fun executor(peek: Boolean = false): E

    public interface Executor {
        public suspend fun request(
            body: JSONObject,
            timeout: Long? = null,
            peek: Boolean = false,
        ): JSONObject
    }
}

public suspend fun RpcEngine.Executor.request(
    id: UInt = Random.nextLong().toUInt(),
    method: String,
    params: JSONArray = JSONArray(),
    timeout: Long? = null,
    peek: Boolean = false,
): JSONObject {
    val body = jsonRpcRequest(id, method, params)

    return request(body, timeout, peek)
}