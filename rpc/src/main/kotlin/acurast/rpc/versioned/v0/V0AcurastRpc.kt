package acurast.rpc.versioned.v0

import acurast.codec.extensions.blake2b
import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toU8a
import acurast.codec.extensions.xxH128
import acurast.codec.type.manager.ProcessorVersion
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

public interface V0AcurastRpc {
    public suspend fun getAccountInfo(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): FrameSystemAccountInfo

    /**
     * Query asset information for a given account.
     */
    public suspend fun getAccountAssetInfo(
        assetId: Int,
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): PalletAssetsAssetAccount?

    /**
     * Get the registration information of a given job.
     */
    public suspend fun getJobRegistration(
        jobIdentifier: JobIdentifier,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): JobRegistration?

    /**
     * Get all job assignments for a given account.
     */
    public suspend fun getAssignedJobs(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): List<JobAssignment>

    /**
     * Verify if the account is associated with an attested device.
     */
    public suspend fun isAttested(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): Boolean

    public suspend fun getJobEnvironment(
        jobIdentifier: JobIdentifier,
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): JobEnvironment?

    public suspend fun getUpdateInfo(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): ProcessorUpdateInfo?

    public suspend fun getKnownBinaryHash(
        version: ProcessorVersion,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): ByteArray?

    public companion object {
        public const val VERSION: UInt = 0u
    }
}

internal fun V0AcurastRpc(
    engine: RpcEngine,
    author: Author,
    chain: Chain,
    state: State,
): V0AcurastRpc = V0AcurastRpcImpl(engine, author, chain, state)

private class V0AcurastRpcImpl(
    private val engine: RpcEngine,
    private val author: Author,
    private val chain: Chain,
    private val state: State,
) : V0AcurastRpc {
    override suspend fun getAccountInfo(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
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

    override suspend fun getAccountAssetInfo(
        assetId: Int,
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): PalletAssetsAssetAccount? {
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
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return ByteBuffer.wrap(storage.hexToBa()).readPalletAssetsAssetAccount()
    }

    override suspend fun getJobRegistration(
        jobIdentifier: JobIdentifier,
        blockHash: ByteArray?,
        timeout: Long?,
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

    override suspend fun getAssignedJobs(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
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

    override suspend fun isAttested(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
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

    override suspend fun getJobEnvironment(
        jobIdentifier: JobIdentifier,
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
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

    override suspend fun getUpdateInfo(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
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

    override suspend fun getKnownBinaryHash(
        version: ProcessorVersion,
        blockHash: ByteArray?,
        timeout: Long?,
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