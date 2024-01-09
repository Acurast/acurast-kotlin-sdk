package acurast.rpc.pallet

import acurast.codec.extensions.toHex
import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.request
import org.json.JSONArray

public class Author(defaultEngine: RpcEngine) : PalletRpc(defaultEngine) {
    /**
     * Submit an extrinsic.
     */
    public suspend fun submitExtrinsic(
        extrinsic: ByteArray,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): String {
        val params = JSONArray().apply {
            put(extrinsic.toHex())
        }

        val response = engine.request(method = "author_submitExtrinsic", params = params, timeout = timeout)

        return response.optString("result") ?: throw handleError(response)
    }
}