package matcher

import io.mockk.Matcher
import io.mockk.MockKMatcherScope
import org.json.JSONArray
import org.json.JSONObject

fun MockKMatcherScope.matchJsonRpcRequest(method: String, params: JSONArray = JSONArray()): JSONObject =
    match(JsonRpcRequestMatcher(method, params))
data class JsonRpcRequestMatcher(val method: String, val params: JSONArray = JSONArray()) :
    Matcher<JSONObject> {
    override fun match(arg: JSONObject?): Boolean =
        arg != null
                && arg.getString("jsonrpc") == "2.0"
                && arg.has("id")
                && arg.getString("method") == method
                && arg.getJSONArray("params").similar(params)

    override fun toString(): String = "matchJsonRpcRequest($method, $params)"

    override fun substitute(map: Map<Any, Any>): Matcher<JSONObject> =
        copy(method = map.getOrDefault(method, method) as String, params = map.getOrDefault(params, params) as JSONArray)
}