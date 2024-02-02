package acurast.rpc

import acurast.codec.extensions.blake2b
import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toU8a
import acurast.codec.extensions.xxH128
import acurast.codec.type.ProcessorVersion
import acurast.codec.type.acurast.JobEnvironment
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.JobRegistration
import acurast.codec.type.manager.ProcessorUpdateInfo
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

public class AcurastRpc(override val defaultEngine: RpcEngine) : Rpc {
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
        engine: RpcEngine = defaultEngine,
    ): FrameSystemAccountInfo {
        val key =
            "System".toByteArray().xxH128() +
            "Account".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return FrameSystemAccountInfo()
        }

        return ByteBuffer.wrap(storage.hexToBa()).readAccountInfo()
    }

    /**
     * Get the registration information of a given job.
     */
    public suspend fun getJobRegistration(
        jobIdentifier: JobIdentifier,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): JobRegistration? {
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
            engine
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return JobRegistration.read(ByteBuffer.wrap(storage.hexToBa()))
    }

    /**
     * Get all job assignments for a given account.
     */
    public suspend fun getAssignedJobs(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): List<JobAssignment> {
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
        )

        val result = state.queryStorageAt(
            storageKeys = keys,
            blockHash,
            timeout,
            engine,
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
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): Boolean {
        val key =
            "Acurast".toByteArray().xxH128() +
                    "StoredAttestation".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        return try {
            val result = state.getStorage(
                storageKey = key,
                blockHash,
                timeout,
                engine,
            )

            !result.isNullOrEmpty()
        } catch (e: Throwable) {
            false
        }
    }

    public suspend fun getJobEnvironment(
        jobIdentifier: JobIdentifier,
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): JobEnvironment? {
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
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return JobEnvironment.read(ByteBuffer.wrap(storage.hexToBa()))
    }

    public suspend fun getUpdateInfo(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): ProcessorUpdateInfo? {
        val key =
            "AcurastProcessorManager".toByteArray().xxH128() +
                    "ProcessorUpdateInfo".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return ProcessorUpdateInfo.read(ByteBuffer.wrap(storage.hexToBa()))
    }

    public suspend fun getKnownBinaryHash(
        version: ProcessorVersion,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
        engine: RpcEngine = defaultEngine,
    ): ByteArray? {
        val versionBytes = version.toU8a()
        val key =
            "AcurastProcessorManager".toByteArray().xxH128() +
                    "KnownBinaryHash".toByteArray().xxH128() +
                    versionBytes.blake2b(128) + versionBytes

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine
        )

        return storage?.takeIf { it.isNotEmpty() }?.hexToBa()
    }
}
