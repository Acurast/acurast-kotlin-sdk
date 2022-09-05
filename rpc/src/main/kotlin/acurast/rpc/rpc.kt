package acurast.rpc

import acurast.codec.extensions.blake2b
import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.extensions.xxH128
import acurast.rpc.type.AccountInfo
import acurast.rpc.type.RuntimeVersion
import acurast.rpc.type.readAccountInfo
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger
import java.net.URL
import java.nio.ByteBuffer

private fun JSONRequest(method: String, params: JSONArray = JSONArray()): JSONObject {
    val body = JSONObject()
    body.put("id", 1)
    body.put("jsonrpc", "2.0")
    body.put("method", method)
    body.put("params", params)
    return body;
}

public class RPC public constructor(rpc_url: String) {
    private val url: URL = URL(rpc_url);

    /**
     * Submit an extrinsic.
     */
    public fun submitExtrinsic(
        txBytes: ByteArray,
        successCallback: (String) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val body = JSONRequest("author_submitExtrinsic", JSONArray().put(txBytes.toHex()));

        Networking.httpsRequest(
            url,
            "POST",
            body.toString().toByteArray(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response, _ ->
                successCallback(JSONObject(response).optString("result"))
            },
            errorCallback
        )
    }

    /**
     * Query account information. (nonce, etc...)
     */
    public fun getAccountInfo(
        publicKey: ByteArray,
        successCallback: (AccountInfo) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val key =
            "System".toByteArray().xxH128() +
            "Account".toByteArray().xxH128() +
            publicKey.blake2b(128) + publicKey;

        val body = JSONRequest("state_getStorage", JSONArray().put(key.toHex()));

        Networking.httpsRequest(
            url,
            "POST",
            body.toString().toByteArray(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response, _ ->
                JSONObject(response).optString("result")?.let { result ->
                    successCallback(ByteBuffer.wrap(result.hexToBa()).readAccountInfo())
                } ?: errorCallback(Exception("query 'result' not available"))
            },
            errorCallback
        )
    }

    /**
     * Query the height of the head block.
     */
    public fun getCurrentHeight(
        successCallback: (Int) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val body = JSONRequest("chain_getHeader");

        Networking.httpsRequest(
            url,
            "POST",
            body.toString().toByteArray(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response, _ ->
                JSONObject(response).optJSONObject("result")
                    ?.optString("number")?.let { result ->
                        successCallback(Integer.decode(result))
                    } ?: errorCallback(Exception("query 'result.block.header.number' not available"))
            },
            errorCallback
        )
    }

    /**
     * Query the hash of a block at a given height.
     */
    public fun getBlockHash(
        height: BigInteger,
        successCallback: (String) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val body = JSONRequest("chain_getBlockHash", JSONArray().put(height.toString(16)));

        Networking.httpsRequest(
            url,
            "POST",
            body.toString().toByteArray(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response, _ ->
                successCallback(JSONObject(response).optString("result"))
            },
            errorCallback
        )
    }

    /**
     * Query runtime version.
     */
    public fun getRuntimeVersion(
        successCallback: (RuntimeVersion) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val body = JSONRequest("state_getRuntimeVersion");

        Networking.httpsRequest(
            url,
            "POST",
            body.toString().toByteArray(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response, _ ->
                JSONObject(response).optJSONObject("result")?.let { result ->
                    successCallback(RuntimeVersion(
                        result.optInt("specVersion"),
                        result.optInt("transactionVersion")
                    ))
                } ?: errorCallback(Exception("query 'result.block.header.number' not available"))
            },
            errorCallback
        )
    }
}
