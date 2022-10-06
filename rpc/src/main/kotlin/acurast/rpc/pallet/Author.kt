package acurast.rpc.pallet

import acurast.codec.extensions.toHex
import acurast.rpc.Networking
import org.json.JSONArray
import org.json.JSONObject

public class Author(rpc_url: String) : PalletRPC(rpc_url) {
    /**
     * Submit an extrinsic.
     */
    public fun submitExtrinsic(
        extrinsic: ByteArray,
        successCallback: (String) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val body = prepareJSONRequest("author_submitExtrinsic", JSONArray().put(extrinsic.toHex()));

        Networking.httpsPostString(
            url,
            body.toString(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response ->
                System.out.println(response)
                val json = JSONObject(response)
                val result = json.optString("result")
                if (result == "null" || result.isEmpty()) {
                    errorCallback(handleError(json))
                } else {
                    successCallback(result)
                }
            },
            errorCallback
        )
    }
}