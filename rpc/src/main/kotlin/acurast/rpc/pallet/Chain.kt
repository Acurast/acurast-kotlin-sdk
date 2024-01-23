package acurast.rpc.pallet

import acurast.codec.extensions.toHex
import acurast.rpc.http.IHttpClientProvider
import acurast.rpc.http.HttpHeader
import acurast.rpc.type.Header
import acurast.rpc.utils.JSON_RPC_KEY_RESULT
import acurast.rpc.utils.nullableOptString
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger

public class Chain(http_client: IHttpClientProvider, rpc_url: String) : PalletRPC(http_client, rpc_url) {
    /**
     * Query the hash of a block at a given height.
     */
    public suspend fun getBlockHash(
        blockNumber: BigInteger? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null
    ): String? {
        val param = JSONArray()
        // Add block number if provided
        if (blockNumber != null) {
            param.put(blockNumber.toString(16))
        }

        val body = prepareJSONRequest("chain_getBlockHash", param)

        val response = http_client.post(
            rpc_url,
            body = body.toString(),
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        val json = JSONObject(response)
        return if (json.has(JSON_RPC_KEY_RESULT)) json.nullableOptString(JSON_RPC_KEY_RESULT) else throw handleError(json)
    }

    /**
     * Get the header of a given block.
     */
    public suspend fun getHeader(
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null
    ): Header {
        val param = JSONArray()
        // Add block hash if provided, otherwise the head block will be queried.
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("chain_getHeader", param)

        val response = http_client.post(
            rpc_url,
            body = body.toString(),
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        val json = JSONObject(response)
        val result = json.optJSONObject("result") ?: throw handleError(json)

        val parentHash = result.nullableOptString("parentHash") ?: throw handleError(json)
        val number = result.nullableOptString("number") ?: throw handleError(json)
        val stateRoot = result.nullableOptString("stateRoot") ?: throw handleError(json)
        val extrinsicsRoot = result.nullableOptString("extrinsicsRoot") ?: throw handleError(json)

        return Header(
            parentHash = parentHash,
            number = BigInteger(number.removePrefix("0x"), 16),
            stateRoot = stateRoot,
            extrinsicsRoot = extrinsicsRoot,
        )
    }
}