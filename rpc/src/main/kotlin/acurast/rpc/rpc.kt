package acurast.rpc

import acurast.codec.extensions.*
import acurast.codec.type.acurast.PalletAcurastMarketplaceAssignment
import acurast.codec.type.acurast.StoredJobAssignment
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
     * Get all job assignments for a given account.
     */
    public suspend fun getJobAssignments(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): List<StoredJobAssignment> {
        val jobs: MutableList<StoredJobAssignment> = mutableListOf()

        val indexKey =
            "AcurastMarketplace".toByteArray().xxH128() +
            "StoredJobAssignment".toByteArray().xxH128() +
            accountId.blake2b(128) + accountId;

        val keys = state.getKeys(
            indexKey,
            blockHash = blockHash,
            headers = headers,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout
        )

        for (key in keys) {
            val result = state.queryStorageAt(
                storageKey = key.hexToBa(),
                blockHash = blockHash,
                headers = headers,
                requestTimeout = requestTimeout,
                connectionTimeout = connectionTimeout
            )
            jobs.add(StoredJobAssignment.read(result[0].changes[0]))
        }

        return jobs
    }


    /**
     * Get all job matches for a given account.
     */
    public suspend fun getJobMatches(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        headers: List<HttpHeader>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): List<PalletAcurastMarketplaceAssignment> {
        val jobs: MutableList<PalletAcurastMarketplaceAssignment> = mutableListOf()

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

        for (key in keys) {
            val result = state.queryStorageAt(
                storageKey = key.hexToBa(),
                blockHash = blockHash,
                headers = headers,
                requestTimeout = requestTimeout,
                connectionTimeout = connectionTimeout
            )
            for (change in result[0].changes) {
                jobs.add(PalletAcurastMarketplaceAssignment.read(change))
            }
        }

        return jobs
    }
}
