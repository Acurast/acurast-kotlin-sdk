package acurast.rpc.engine

import acurast.rpc.utils.jsonRpcRequest
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

public interface RpcEngine<Self : RpcEngine<Self>> {
    public suspend fun request(
        body: JSONObject,
        timeout: Long? = null,
        peek: Boolean = false,
    ): JSONObject

    @Suppress("UNCHECKED_CAST")
    public suspend fun <T> contextual(action: suspend (Self) -> T): T = action(this as Self)
}

public suspend fun RpcEngine<*>.request(
    id: UInt = Random.nextLong().toUInt(),
    method: String,
    params: JSONArray = JSONArray(),
    timeout: Long? = null,
    peek: Boolean = false,
): JSONObject {
    val body = jsonRpcRequest(id, method, params)

    return request(body, timeout, peek)
}