package acurast.rpc.pallet

import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

public abstract class PalletRPC constructor(rpc_url: String) {
    protected val url: URL = URL(rpc_url);

    protected fun handleError(json: JSONObject): Exception {
        return json.optJSONObject("error")?.let { error ->
            Exception(error.optString("message"))
        } ?: Exception("something went wrong")
    }

    protected fun prepareJSONRequest(method: String, params: JSONArray = JSONArray()): JSONObject {
        val body = JSONObject()
        body.put("id", 1)
        body.put("jsonrpc", "2.0")
        body.put("method", method)
        body.put("params", params)
        return body;
    }
}