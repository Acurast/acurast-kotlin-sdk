package acurast.rpc.pallet

import acurast.rpc.http.IHttpClientProvider
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

public fun JSONRequest(method: String, params: JSONArray = JSONArray()): JSONObject {
    val body = JSONObject()
    body.put("id", 1)
    body.put("jsonrpc", "2.0")
    body.put("method", method)
    body.put("params", params)
    return body;
}

public abstract class PalletRPC constructor(
    protected val http_client: IHttpClientProvider,
    protected val rpc_url: String
) {
    protected val url: URL = URL(rpc_url);

    protected fun handleError(json: JSONObject): Exception {
        System.out.println(json);
        return json.optJSONObject("error")?.let { error ->
            Exception(error.optString("message"))
        } ?: Exception("something went wrong")
    }

    protected fun prepareJSONRequest(method: String, params: JSONArray = JSONArray()): JSONObject {
        return JSONRequest(method, params);
    }
}