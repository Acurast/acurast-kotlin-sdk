package acurast.rpc

import acurast.codec.extensions.*
import acurast.codec.type.acurast.JobEnvironment
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.JobRegistration
import acurast.codec.type.marketplace.JobAssignment
import acurast.rpc.engine.RpcEngine
import acurast.rpc.pallet.Author
import acurast.rpc.pallet.Chain
import acurast.rpc.pallet.State
import acurast.rpc.type.FrameSystemAccountInfo
import acurast.rpc.type.PalletAssetsAssetAccount
import acurast.rpc.type.readAccountInfo
import acurast.rpc.type.readPalletAssetsAssetAccount
import java.nio.ByteBuffer

public class AcurastRpc(override val defaultEngine: RpcEngine<*>) : Rpc {
    public val author: Author = Author(defaultEngine)
    public val chain: Chain = Chain(defaultEngine)
    public val state: State = State(defaultEngine)

    /**
     * Query account information. (nonce, etc...)
     */
    public suspend fun getAccountInfo(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine<*> = defaultEngine,
        peekRequest: Boolean = false,
    ): FrameSystemAccountInfo = engine.contextual { engine ->
        val key =
            "System".toByteArray().xxH128() +
            "Account".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
            peekRequest,
        )

        if (storage == "null" || storage.isEmpty()) {
            return@contextual FrameSystemAccountInfo()
        }

        return@contextual ByteBuffer.wrap(storage.hexToBa()).readAccountInfo()
    }

    /**
     * Query asset information for a given account.
     */
    public suspend fun getAccountAssetInfo(
        assetId: Int,
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine<*> = defaultEngine,
        peekRequest: Boolean = false,
    ): PalletAssetsAssetAccount = engine.contextual { engine ->
        val assetIdBytes = assetId.toU8a();
        val key =
            "Assets".toByteArray().xxH128() +
                    "Account".toByteArray().xxH128() +
                    assetIdBytes.blake2b(128) + assetIdBytes +
                    accountId.blake2b(128) + accountId

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
            peekRequest,
        )

        return@contextual ByteBuffer.wrap(storage.hexToBa()).readPalletAssetsAssetAccount()
    }

    /**
     * Get the registration information of a given job.
     */
    public suspend fun getJobRegistration(
        jobIdentifier: JobIdentifier,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine<*> = defaultEngine,
        peekRequest: Boolean = false,
    ): JobRegistration = engine.contextual { engine ->
        val origin = jobIdentifier.origin.toU8a()
        val jobId = jobIdentifier.id.toU8a()

        val indexKey =
            "Acurast".toByteArray().xxH128() + "StoredJobRegistration".toByteArray().xxH128() +
                    origin.blake2b(128) + origin +
                    jobId.blake2b(128) + jobId

        val storage = state.getStorage(
            storageKey = indexKey,
            blockHash,
            timeout,
            engine,
            peekRequest,
        )

        return@contextual JobRegistration.read(ByteBuffer.wrap(storage.hexToBa()))
    }

    /**
     * Get all job assignments for a given account.
     */
    public suspend fun getAssignedJobs(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine<*> = defaultEngine,
        peekRequest: Boolean = false,
    ): List<JobAssignment> = engine.contextual { engine ->
        val jobs: MutableList<JobAssignment> = mutableListOf()

        val indexKey =
            "AcurastMarketplace".toByteArray().xxH128() +
                    "StoredMatches".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        val keys = state.getKeys(
            key = indexKey,
            blockHash,
            timeout,
            engine,
            peekRequest,
        )

        val result = state.queryStorageAt(
            storageKeys = keys,
            blockHash,
            timeout,
            engine,
            peekRequest,
        )

        if (result.isNotEmpty()) {
            for (change in result[0].changes) {
                jobs.add(JobAssignment.read(change))
            }
        }

        return@contextual jobs
    }

    /**
     * Verify if the account is associated with an attested device.
     */
    public suspend fun isAttested(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine<*> = defaultEngine,
        peekRequest: Boolean = false,
    ): Boolean = engine.contextual { engine ->
        val key =
            "Acurast".toByteArray().xxH128() +
                    "StoredAttestation".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        return@contextual try {
            val result = state.getStorage(
                storageKey = key,
                blockHash,
                timeout,
                engine,
                peekRequest,
            )

            result != "null" && result.isNotEmpty()
        } catch (e: Throwable) {
            false
        }
    }

    public suspend fun getJobEnvironment(
        jobIdentifier: JobIdentifier,
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine<*> = defaultEngine,
        peekRequest: Boolean = false,
    ): JobEnvironment? = engine.contextual { engine ->
        val jobId = jobIdentifier.origin.toU8a() + jobIdentifier.id.toU8a()

        val key =
            "Acurast".toByteArray().xxH128() +
                    "ExecutionEnvironment".toByteArray().xxH128() +
                    jobId.blake2b(128) + jobId +
                    accountId.blake2b(128) + accountId

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
            peekRequest,
        )

        if (storage == "null" || storage.isEmpty()) {
            return@contextual null
        }

        return@contextual JobEnvironment.read(ByteBuffer.wrap(storage.hexToBa()))
    }
}
