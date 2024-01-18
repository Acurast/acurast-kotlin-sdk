package acurast.rpc.pallet

import acurast.codec.extensions.toHex
import acurast.rpc.http.IHttpClientProvider
import acurast.rpc.http.HttpHeader
import acurast.rpc.utils.nullableOptString
import org.json.JSONArray
import org.json.JSONObject

public class Author(http_client: IHttpClientProvider, rpc_url: String) : PalletRPC(http_client, rpc_url) {
    /**
     * Submit an extrinsic.
     */
    public suspend fun submitExtrinsic(
        extrinsic: ByteArray,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null
    ): String? {
        val body = prepareJSONRequest("author_submitExtrinsic", JSONArray().put(extrinsic.toHex()));

        val response = http_client.post(
            rpc_url,
            body = body.toString(),
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        val json = JSONObject(response)

        if (json.has("error")) {
            throw handleError(json)
        }

        return json.nullableOptString("result")
    }
}