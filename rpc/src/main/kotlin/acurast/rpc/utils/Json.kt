package acurast.rpc.utils

import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

internal object JsonRpc {
    object Key {
        const val RESULT = "result"
        const val ERROR = "error"
        const val MESSAGE = "message"
        const val DATA = "data"
    }
}

/**
 * JSONObject.optString(key) actually never returns `null`:
 * - if the key is not present in the object, the method returns an empty string `""`
 * - if the key is present but has no value, i.e. the value is `null`, the method returns the string `"null"`
 *
 * This behavior is counterintuitive and can lead to various unexpected effects.
 * `JSONObject.nullableOptString` tries to mitigate the issue by checking whether the value actually exists before
 * trying to access and return it, and returning `null` if it doesn't.
 */
internal fun JSONObject.nullableOptString(key: String): String? =
    if (has(key) && !isNull(key)) getString(key) else null

public fun jsonRpcRequest(id: UInt = Random.nextLong().toUInt(), method: String, params: JSONArray = JSONArray()): JSONObject =
    JSONObject().apply {
        put("id", id)
        put("jsonrpc", "2.0")
        put("method", method)
        put("params", params)
    }