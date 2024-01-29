package acurast.rpc.engine

import acurast.rpc.utils.jsonRpcRequest
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

public interface RpcEngine {
    public val id: String
    public suspend fun request(body: JSONObject, timeout: Long? = null): JSONObject
}

public suspend fun RpcEngine.request(
    id: UInt = Random.nextLong().toUInt(),
    method: String,
    params: JSONArray = JSONArray(),
    timeout: Long? = null,
): JSONObject {
    val body = jsonRpcRequest(id, method, params)

    return request(body, timeout)
}