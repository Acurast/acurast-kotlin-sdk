package acurast.rpc.pallet

import acurast.codec.extensions.toHex
import acurast.rpc.Networking
import acurast.rpc.type.StorageQueryResult
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

public data class RuntimeVersion(
    val specVersion: Int,
    val transactionVersion: Int,
)

public class State(rpc_url: String) : PalletRPC(rpc_url) {
    /**
     * Query storage.
     */
    public fun getStorage(
        storageKey: ByteArray,
        blockHash: ByteArray? = null,
        successCallback: (String) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val param = JSONArray().put(storageKey.toHex())
        // Add block hash if provided
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("state_getStorage", param)

        Networking.httpsPostString(
            url,
            body.toString(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response ->
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

    /**
     * Query storage.
     */
    public fun queryStorageAt(
        storageKey: ByteArray,
        blockHash: ByteArray? = null,
        successCallback: (List<StorageQueryResult>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val param = JSONArray().put(JSONArray().put((storageKey.toHex())))
        // Add block hash if provided
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("state_queryStorageAt", param)

        Networking.httpsPostString(
            url,
            body.toString(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response ->
                val result = JSONObject(response).optJSONArray("result").toString()
                val queryResult = GsonBuilder().create()
                    .fromJson(result, Array<StorageQueryResult>::class.java).toList()
                successCallback(queryResult)
            },
            errorCallback
        )
    }

    /**
     * Query storage keys. (Used when querying a StorageDoubleMap)
     */
    public fun getKeys(
        key: ByteArray,
        blockHash: ByteArray? = null,
        successCallback: (List<String>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val param = JSONArray().put(key.toHex())
        // Add block hash if provided
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("state_getKeys", param)

        Networking.httpsPostString(
            url,
            body.toString(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response ->
                val result = JSONObject(response).optJSONArray("result").toString()
                val queryResult = GsonBuilder().create()
                    .fromJson(result, Array<String>::class.java).toList()
                successCallback(queryResult)
            },
            errorCallback
        )
    }

    /**
     * Query runtime version.
     */
    public fun getRuntimeVersion(
        blockHash: ByteArray? = null,
        successCallback: (RuntimeVersion) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val param = JSONArray()
        // Add block hash if provided
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("state_getRuntimeVersion", param)

        Networking.httpsPostString(
            url,
            body.toString(),
            mapOf(Pair("Accept", "*/*"), Pair("Content-Type", "application/json")),
            { response ->
                val json = JSONObject(response)
                val result = json.optJSONObject("result")
                if (result == null) {
                    errorCallback(handleError(json))
                } else {
                    successCallback(
                        RuntimeVersion(
                            result.optInt("specVersion"),
                            result.optInt("transactionVersion")
                        )
                    )
                }
            },
            errorCallback
        )
    }
}