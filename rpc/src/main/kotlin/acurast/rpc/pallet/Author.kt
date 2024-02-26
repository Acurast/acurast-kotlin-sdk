package acurast.rpc.pallet

import acurast.codec.extensions.toHex
import acurast.rpc.utils.JsonRpc
import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.request
import acurast.rpc.utils.nullableOptString
import org.json.JSONArray

public class Author : PalletRpc() {
    /**
     * Submit an extrinsic.
     */
    public suspend fun submitExtrinsic(
        extrinsic: ByteArray,
        timeout: Long? = null,
        engine: RpcEngine,
    ): String? {
        val params = JSONArray().apply {
            put(extrinsic.toHex())
        }

        val response = engine.request(method = "author_submitExtrinsic", params = params, timeout = timeout)

        return if (response.has(JsonRpc.Key.RESULT)) response.nullableOptString(JsonRpc.Key.RESULT) else throw handleError(response)
    }
}