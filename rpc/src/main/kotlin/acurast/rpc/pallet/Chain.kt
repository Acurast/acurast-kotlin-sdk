package acurast.rpc.pallet

import acurast.codec.extensions.toHex
import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.request
import acurast.rpc.type.Header
import org.json.JSONArray
import java.math.BigInteger

public class Chain(defaultEngine: RpcEngine) : PalletRpc(defaultEngine) {
    /**
     * Query the hash of a block at a given height.
     */
    public suspend fun getBlockHash(
        blockNumber: BigInteger? = null,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
        peekRequest: Boolean = false,
    ): String {
        val params = JSONArray().apply {
            // Add block number if provided
            blockNumber?.let { put(it.toString(16)) }
        }

        val response = engine.request(method = "chain_getBlockHash", params = params, timeout = timeout, peek = peekRequest)

        return response.optString("result") ?: throw handleError(response)
    }

    /**
     * Get the header of a given block.
     */
    public suspend fun getHeader(
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
        peekRequest: Boolean = false,
    ): Header {
        val params = JSONArray().apply {
            // Add block hash if provided, otherwise the head block will be queried.
            blockHash?.let { put(it.toHex()) }
        }

        val response = engine.request(method = "chain_getHeader", params = params, timeout = timeout, peek = peekRequest)
        val result = response.optJSONObject("result") ?: throw handleError(response)

        val parentHash = result.optString("parentHash")
        val number = result.optString("number")
        val stateRoot = result.optString("stateRoot")
        val extrinsicsRoot = result.optString("extrinsicsRoot")

        return Header(
            parentHash = parentHash,
            number = BigInteger(number.removePrefix("0x"), 16),
            stateRoot = stateRoot,
            extrinsicsRoot = extrinsicsRoot,
        )
    }
}