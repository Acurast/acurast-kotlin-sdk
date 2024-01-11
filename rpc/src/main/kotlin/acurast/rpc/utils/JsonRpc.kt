package acurast.rpc.utils

import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

public fun jsonRpcRequest(id: UInt = Random.nextLong().toUInt(), method: String, params: JSONArray = JSONArray()): JSONObject =
    JSONObject().apply {
        put("id", id)
        put("jsonrpc", "2.0")
        put("method", method)
        put("params", params)
    }