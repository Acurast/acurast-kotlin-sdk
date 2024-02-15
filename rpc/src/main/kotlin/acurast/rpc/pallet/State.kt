package acurast.rpc.pallet

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.rpc.utils.JsonRpc
import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.request
import acurast.rpc.type.RuntimeMetadataV14
import acurast.rpc.type.StorageQueryResult
import acurast.rpc.type.readMetadata
import acurast.rpc.utils.nullableOptString
import org.json.JSONArray
import java.nio.ByteBuffer

public data class RuntimeVersion(
    val specVersion: Int,
    val transactionVersion: Int,
)

public class State : PalletRpc() {
    /**
     * Perform a call to a builtin on the chain.
     */
    public suspend fun call(
        method: String,
        data: ByteArray? = null,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine,
    ): String? {
        val params = JSONArray().apply {
            put(method)

            // Add method payload
            data?.let { put(it.toHex()) }

            // Add block hash if provided
            blockHash?.let { put(it.toHex()) }
        }

        val response = engine.request(method = "state_call", params = params, timeout = timeout)

        return if (response.has(JsonRpc.Key.RESULT)) response.nullableOptString(JsonRpc.Key.RESULT) else throw handleError(response)
    }

    /**
     * Query storage.
     */
    public suspend fun getStorage(
        storageKey: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine,
    ): String? {
        val params = JSONArray().apply {
            put(storageKey.toHex())

            // Add block hash if provided
            blockHash?.let { put(it.toHex()) }
        }

        val response = engine.request(method = "state_getStorage", params = params, timeout = timeout)

        return  if (response.has(JsonRpc.Key.RESULT)) response.nullableOptString(JsonRpc.Key.RESULT) else throw handleError(response)
    }

    /**
     * Query storage.
     */
    public suspend fun queryStorageAt(
        storageKeys: List<String>,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine,
    ): List<StorageQueryResult> {
        val params = JSONArray().apply {
            put(storageKeys.fold(JSONArray()) { acc, key -> acc.put(key) })

            // Add block hash if provided
            blockHash?.let { put(it.toHex()) }
        }

        val response = engine.request(method = "state_queryStorageAt", params = params, timeout = timeout)

        return response.optJSONArray(JsonRpc.Key.RESULT)?.toTypedList<StorageQueryResult>() ?: throw handleError(response)
    }

    /**
     * Query storage keys. (Used when querying a StorageDoubleMap)
     */
    public suspend fun getKeys(
        key: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine,
    ): List<String> {
        val params = JSONArray().apply {
            put(key.toHex())

            // Add block hash if provided
            blockHash?.let { put(it.toHex()) }
        }

        val response = engine.request(method = "state_getKeys", params = params, timeout = timeout)

        return response.optJSONArray(JsonRpc.Key.RESULT)?.toTypedList<String>() ?: throw handleError(response)
    }

    /**
     * Query runtime version.
     */
    public suspend fun getRuntimeVersion(
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine,
    ): RuntimeVersion {
        val params = JSONArray().apply {
            // Add block hash if provided
            blockHash?.let { put(it.toHex()) }
        }

        val response = engine.request(method = "state_getRuntimeVersion", params = params, timeout = timeout)

        val result = response.optJSONObject(JsonRpc.Key.RESULT) ?: throw handleError(response)

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
        timeout: Long? = null,
        engine: RpcEngine,
    ): RuntimeMetadataV14 {
        val params = JSONArray().apply {
            // Add block hash if provided
            blockHash?.let { put(it.toHex()) }
        }

        val response = engine.request(method = "state_getMetadata", params = params, timeout = timeout)
        val result = response.nullableOptString(JsonRpc.Key.RESULT) ?: throw handleError(response)

        return ByteBuffer.wrap(result.hexToBa()).readMetadata()
    }
}