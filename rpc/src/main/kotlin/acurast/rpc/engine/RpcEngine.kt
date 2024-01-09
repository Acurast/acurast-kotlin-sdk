package acurast.rpc.engine

import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

public interface RpcEngine {
    public suspend fun request(body: JSONObject, timeout: Long? = null): JSONObject
}

public suspend fun RpcEngine.request(
    id: UInt = Random.nextLong().toUInt(),
    method: String,
    params: JSONArray = JSONArray(),
    timeout: Long? = null,
): JSONObject {
    val body = JSONObject().apply {
        put("id", id)
        put("jsonrpc", "2.0")
        put("method", method)
        put("params", params)
    }

    return request(body, timeout)
}