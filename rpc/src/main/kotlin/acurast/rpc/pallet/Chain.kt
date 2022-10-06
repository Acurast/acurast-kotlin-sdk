package acurast.rpc.pallet

import acurast.codec.extensions.toHex
import acurast.rpc.Networking
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger

public data class Header(
    val parentHash: String,
    val number: BigInteger,
    val stateRoot: String,
    val extrinsicsRoot: String,
    // TODO: add digest field
)

public class Chain(rpc_url: String) : PalletRPC(rpc_url) {
    /**
     * Query the hash of a block at a given height.
     */
    public fun getBlockHash(
        blockNumber: BigInteger? = null,
        successCallback: (String) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val param = JSONArray()
        // Add block number if provided
        if (blockNumber != null) {
            param.put(blockNumber.toString(16))
        }

        val body = prepareJSONRequest("chain_getBlockHash", param)

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
     * Get the header of a given block.
     */
    public fun getHeader(
        blockHash: ByteArray? = null,
        successCallback: (Header) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val param = JSONArray()
        // Add block hash if provided, otherwise the head block will be queried.
        if (blockHash != null) {
            param.put(blockHash.toHex())
        }

        val body = prepareJSONRequest("chain_getHeader", param)

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
                    val parentHash = result.optString("parentHash")
                    val number = result.optString("number")
                    val stateRoot = result.optString("stateRoot")
                    val extrinsicsRoot = result.optString("extrinsicsRoot")

                    successCallback(
                        Header(
                            parentHash = parentHash,
                            number = BigInteger(number.removePrefix("0x"), 16),
                            stateRoot = stateRoot,
                            extrinsicsRoot = extrinsicsRoot,
                        )
                    )
                }
            },
            errorCallback
        )
    }
}