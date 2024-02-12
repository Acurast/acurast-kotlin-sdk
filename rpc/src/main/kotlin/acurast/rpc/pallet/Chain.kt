package acurast.rpc.pallet

import acurast.codec.extensions.fromSS58
import acurast.codec.extensions.toHex
import acurast.codec.extensions.toSS58
import acurast.codec.type.AccountId32
import acurast.codec.type.acurast.JobEnvironment
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.MultiOrigin
import acurast.codec.type.marketplace.JobAssignment
import acurast.rpc.JsonRpc
import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.request
import acurast.rpc.pallet.orchestrator.JobAssignmentWithRegistration
import acurast.rpc.pallet.orchestrator.toU8a
import acurast.rpc.type.Header
import acurast.rpc.utils.nullableOptString
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger

public class Chain(defaultEngine: RpcEngine) : PalletRpc(defaultEngine) {
    /**
     * Query the hash of a block at a given height.
     */
    public suspend fun getBlockHash(
        blockNumber: BigInteger? = null,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): String? {
        val params = JSONArray().apply {
            // Add block number if provided
            blockNumber?.let { put(it.toLong()) }
        }

        val response =
            engine.request(method = "chain_getBlockHash", params = params, timeout = timeout)

        return if (response.has(JsonRpc.Key.RESULT)) response.nullableOptString(JsonRpc.Key.RESULT) else throw handleError(
            response
        )
    }

    /**
     * Get the header of a given block.
     */
    public suspend fun getHeader(
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): Header {
        val params = JSONArray().apply {
            // Add block hash if provided, otherwise the head block will be queried.
            blockHash?.let { put(it.toHex()) }
        }

        val response =
            engine.request(method = "chain_getHeader", params = params, timeout = timeout)
        val result = response.optJSONObject(JsonRpc.Key.RESULT) ?: throw handleError(response)

        val parentHash = result.nullableOptString("parentHash") ?: throw handleError(response)
        val number = result.nullableOptString("number") ?: throw handleError(response)
        val stateRoot = result.nullableOptString("stateRoot") ?: throw handleError(response)
        val extrinsicsRoot =
            result.nullableOptString("extrinsicsRoot") ?: throw handleError(response)

        return Header(
            parentHash = parentHash,
            number = BigInteger(number.removePrefix("0x"), 16),
            stateRoot = stateRoot,
            extrinsicsRoot = extrinsicsRoot,
        )
    }

    public suspend fun orchestratorMatchedJobs(
        accountId: AccountId32,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): List<JobAssignmentWithRegistration> {
        val params = JSONArray().apply {
            put(accountId.toU8a().toSS58())
        }

        val response =
            engine.request(method = "orchestrator_matchedJobs", params = params, timeout = timeout)

//        println(response);

        if (response.has(JsonRpc.Key.RESULT)) {
            return (response.get(JsonRpc.Key.RESULT) as JSONArray).map {
                JobAssignmentWithRegistration.fromJson(accountId, it as JSONObject)
            }
        } else {
            throw handleError(response)
        }
    }

    public suspend fun orchestratorJobEnvironment(
        jobId: JobIdentifier,
        accountId: AccountId32,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): JobEnvironment? {
        val params = JSONArray().apply {
            put(JSONArray().apply {
                put(
                    when (jobId.origin.kind) {
                        MultiOrigin.Kind.Acurast -> JSONObject(
                            hashMapOf(
                                "acurast" to jobId.origin.source.toSS58()
                            )
                        )

                        MultiOrigin.Kind.Tezos -> JSONObject(
                            hashMapOf(
                                "tezos" to jobId.origin.source.toHex()
                            )
                        )
                    }
                )
                put(jobId.id)
            })
            put(accountId.toU8a().toSS58())
        }

        val response =
            engine.request(
                method = "orchestrator_jobEnvironment",
                params = params,
                timeout = timeout
            )

        println(response);

        if (response.has(JsonRpc.Key.RESULT)) {
            return (response.get(JsonRpc.Key.RESULT) as? JSONObject)?.let {
                JobEnvironment.fromJson(it)
            }
        } else {
            throw handleError(response)
        }
    }

    public suspend fun orchestratorIsAttested(
        accountId: AccountId32,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): Boolean {
        val params = JSONArray().apply {
            put(accountId.toU8a().toSS58())
        }

        val response =
            engine.request(
                method = "orchestrator_is_attested",
                params = params,
                timeout = timeout
            )

        println(response);

        if (response.has(JsonRpc.Key.RESULT)) {
            return response.get(JsonRpc.Key.RESULT) as Boolean
        } else {
            throw handleError(response)
        }
    }
}

private fun JobEnvironment.Companion.fromJson(json: JSONObject): JobEnvironment {
    return JobEnvironment(
        publicKey = json.getJSONArray("publicKey").toU8a(),
        vars = json.getJSONArray("variables").map { JobEnvironment.Variable.fromJson(it as JSONArray) }
    )
}

private fun JobEnvironment.Variable.Companion.fromJson(json: JSONArray): JobEnvironment.Variable {
    return JobEnvironment.Variable(key= json.getJSONArray(0).toU8a().decodeToString(), value = json.getJSONArray(1).toU8a())
}
