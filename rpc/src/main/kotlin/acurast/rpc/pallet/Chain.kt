package acurast.rpc.pallet

import acurast.codec.extensions.toHex
import acurast.rpc.utils.JsonRpc
import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.request
import acurast.rpc.type.Header
import acurast.rpc.utils.nullableOptString
import org.json.JSONArray
import java.math.BigInteger

public class Chain : PalletRpc() {
    /**
     * Query the hash of a block at a given height.
     */
    public suspend fun getBlockHash(
        blockNumber: BigInteger? = null,
        timeout: Long? = null,
        engine: RpcEngine,
    ): String? {
        val params = JSONArray().apply {
            // Add block number if provided
            blockNumber?.let { put(it.toLong()) }
        }

        val response = engine.request(method = "chain_getBlockHash", params = params, timeout = timeout)

        return if (response.has(JsonRpc.Key.RESULT)) response.nullableOptString(JsonRpc.Key.RESULT) else throw handleError(response)
    }

    /**
     * Get the header of a given block.
     */
    public suspend fun getHeader(
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine,
    ): Header {
        val params = JSONArray().apply {
            // Add block hash if provided, otherwise the head block will be queried.
            blockHash?.let { put(it.toHex()) }
        }

        val response = engine.request(method = "chain_getHeader", params = params, timeout = timeout)
        val result = response.optJSONObject(JsonRpc.Key.RESULT) ?: throw handleError(response)

        val parentHash = result.nullableOptString("parentHash") ?: throw handleError(response)
        val number = result.nullableOptString("number") ?: throw handleError(response)
        val stateRoot = result.nullableOptString("stateRoot") ?: throw handleError(response)
        val extrinsicsRoot = result.nullableOptString("extrinsicsRoot") ?: throw handleError(response)

        return Header(
            parentHash = parentHash,
            number = BigInteger(number.removePrefix("0x"), 16),
            stateRoot = stateRoot,
            extrinsicsRoot = extrinsicsRoot,
        )
    }
}