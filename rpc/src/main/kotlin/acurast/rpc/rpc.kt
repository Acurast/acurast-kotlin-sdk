package acurast.rpc

import acurast.codec.extensions.*
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.JobRegistration
import acurast.codec.type.marketplace.JobAssignment
import acurast.rpc.http.HttpHeader
import acurast.rpc.http.IHttpClientProvider
import acurast.rpc.http.KtorHttpClientProvider
import acurast.rpc.http.KtorLogger
import acurast.rpc.pallet.Author
import acurast.rpc.pallet.Chain
import acurast.rpc.pallet.State
import acurast.rpc.type.FrameSystemAccountInfo
import acurast.rpc.type.PalletAssetsAssetAccount
import acurast.rpc.type.readAccountInfo
import acurast.rpc.type.readPalletAssetsAssetAccount
import java.nio.ByteBuffer

public class RPC public constructor(
    rpc_url: String,
    http_client: IHttpClientProvider = KtorHttpClientProvider(object : KtorLogger() {
        override fun log(message: String) {
            println(message)
        }
    })
) {
    public val author: Author = Author(http_client, rpc_url)
    public val chain: Chain = Chain(http_client, rpc_url)
    public val state: State = State(http_client, rpc_url)

    /**
     * Query account information. (nonce, etc...)
     */
    public suspend fun getAccountInfo(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): FrameSystemAccountInfo {
        val key =
            "System".toByteArray().xxH128() +
            "Account".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId;

        val storage = state.getStorage(
            storageKey = key,
            blockHash = blockHash,
            headers,
            requestTimeout,
            connectionTimeout
        )

        if (storage == "null" || storage.isEmpty()) {
            return FrameSystemAccountInfo()
        }

        return ByteBuffer.wrap(storage.hexToBa()).readAccountInfo()
    }

    /**
     * Query asset information for a given account.
     */
    public suspend fun getAccountAssetInfo(
        assetId: Int,
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): PalletAssetsAssetAccount {
        val assetIdBytes = assetId.toU8a();
        val key =
            "Assets".toByteArray().xxH128() +
                    "Account".toByteArray().xxH128() +
                    assetIdBytes.blake2b(128) + assetIdBytes +
                    accountId.blake2b(128) + accountId;

        val storage = state.getStorage(
            storageKey = key,
            blockHash = blockHash,
            headers,
            requestTimeout,
            connectionTimeout
        )

        return ByteBuffer.wrap(storage.hexToBa()).readPalletAssetsAssetAccount()
    }

    /**
     * Get the registration information of a given job.
     */
    public suspend fun getJobRegistration(
        jobIdentifier: JobIdentifier,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): JobRegistration {
        val origin = jobIdentifier.origin.toU8a()
        val jobId = jobIdentifier.id.toU8a()

        val indexKey =
            "Acurast".toByteArray().xxH128() + "StoredJobRegistration".toByteArray().xxH128() +
                    origin.blake2b(128) + origin +
                    jobId.blake2b(128) + jobId

        val storage = state.getStorage(
            storageKey = indexKey,
            blockHash = blockHash,
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        return JobRegistration.read(ByteBuffer.wrap(storage.hexToBa()))
    }

    /**
     * Get all job assignments for a given account.
     */
    public suspend fun getAssignedJobs(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): List<JobAssignment> {
        val jobs: MutableList<JobAssignment> = mutableListOf()

        val indexKey =
            "AcurastMarketplace".toByteArray().xxH128() +
                    "StoredMatches".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId;

        val keys = state.getKeys(
            indexKey,
            blockHash = blockHash,
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        val result = state.queryStorageAt(
            storageKeys = keys,
            blockHash = blockHash,
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        if (result.isNotEmpty()) {
            for (change in result[0].changes) {
                jobs.add(JobAssignment.read(change))
            }
        }

        return jobs
    }

    /**
     * Verify if the account is associated with an attested device.
     */
    public suspend fun isAttested(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): Boolean {
        val key =
            "Acurast".toByteArray().xxH128() +
                    "StoredAttestation".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        return try {
            val result = state.getStorage(
                storageKey = key,
                blockHash = blockHash,
                headers = headers,
                requestTimeout = requestTimeout,
                connectionTimeout = connectionTimeout
            )

            result != "null" && result.isNotEmpty()
        } catch (e: Throwable) {
            false
        }
    }
}
