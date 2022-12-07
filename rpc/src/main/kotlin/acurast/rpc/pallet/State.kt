package acurast.rpc.pallet

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.rpc.http.HttpHeader
import acurast.rpc.http.IHttpClientProvider
import acurast.rpc.type.*
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer

public data class RuntimeVersion(
    val specVersion: Int,
    val transactionVersion: Int,
)

public class State(http_client: IHttpClientProvider, rpc_url: String) : PalletRPC(http_client, rpc_url) {
    /**
     * Query storage.
     */
    public suspend fun getStorage(
        storageKey: ByteArray,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null
    ): String {
        val param = JSONArray().put(storageKey.toHex())
        // Add block hash if provided
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("state_getStorage", param)

        val response = http_client.post(
            rpc_url,
            body = body.toString(),
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        val json = JSONObject(response)
        val result = json.optString("result")
        if (result == "null" || result.isEmpty()) {
            throw handleError(json)
        } else {
            return result
        }
    }

    /**
     * Query storage.
     */
    public suspend fun queryStorageAt(
        storageKey: ByteArray,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null
    ): List<StorageQueryResult> {
        val param = JSONArray().put(JSONArray().put((storageKey.toHex())))
        // Add block hash if provided
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("state_queryStorageAt", param)


        val response = http_client.post(
            rpc_url,
            body = body.toString(),
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        val result = JSONObject(response).optJSONArray("result").toString()
        return GsonBuilder().create().fromJson(result, Array<StorageQueryResult>::class.java).toList()
    }

    /**
     * Query storage keys. (Used when querying a StorageDoubleMap)
     */
    public suspend fun getKeys(
        key: ByteArray,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null
    ): List<String> {
        val param = JSONArray().put(key.toHex())
        // Add block hash if provided
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("state_getKeys", param)

        val response = http_client.post(
            rpc_url,
            body = body.toString(),
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        val result = JSONObject(response).optJSONArray("result").toString()
        return GsonBuilder().create().fromJson(result, Array<String>::class.java).toList()
    }

    /**
     * Query runtime version.
     */
    public suspend fun getRuntimeVersion(
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null
    ): RuntimeVersion {
        val param = JSONArray()
        // Add block hash if provided
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("state_getRuntimeVersion", param)

        val response = http_client.post(
            rpc_url,
            body = body.toString(),
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        val json = JSONObject(response)
        val result = json.optJSONObject("result") ?: throw handleError(json)

        return RuntimeVersion(
            result.optInt("specVersion"),
            result.optInt("transactionVersion")
        )
    }

    /**
     * Query runtime metadata.
     */
    public suspend fun getMetadata(
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null
    ): RuntimeMetadataV14 {
        val param = JSONArray()
        // Add block hash if provided
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("state_getMetadata", param)

        val response = http_client.post(
            rpc_url,
            body = body.toString(),
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        val json = JSONObject(response)
        val result = json.optString("result") ?: throw handleError(json)

        return ByteBuffer.wrap(result.hexToBa()).readMetadata()
    }
}