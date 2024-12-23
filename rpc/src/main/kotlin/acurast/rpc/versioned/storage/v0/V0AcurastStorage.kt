package acurast.rpc.versioned.storage.v0

import acurast.codec.extensions.*
import acurast.codec.type.acurast.*
import acurast.codec.type.marketplace.JobAssignment
import acurast.codec.type.uniques.PalletUniquesItemDetails
import acurast.rpc.engine.RpcEngine
import acurast.rpc.pallet.State
import acurast.rpc.type.FrameSystemAccountInfo
import acurast.rpc.type.PalletAssetsAssetAccount
import acurast.rpc.type.readAccountInfo
import acurast.rpc.type.readPalletAssetsAssetAccount
import acurast.rpc.versioned.storage.VersionedAcurastStorage
import java.nio.ByteBuffer

public interface V0AcurastStorage : VersionedAcurastStorage {
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
     * Get the manager paired with this device
     */
    public suspend fun getManagerIdentifier(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): Int?

    public suspend fun getManagerCounter(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): ULong?

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

    public suspend fun getAttestation(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): Attestation?

    public suspend fun getJobEnvironment(
        jobIdentifier: JobIdentifier,
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): JobEnvironment?

    public suspend fun getAsset(managerId: Int, blockHash: ByteArray? = null, timeout: Long? = null): PalletUniquesItemDetails?

    public suspend fun getMarketplacePricing(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): MarketplacePricing?

    public suspend fun getMarketplaceAdvertisementRestriction(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): MarketplaceAdvertisementRestriction?

    public companion object {
        public const val VERSION: UInt = 0u
    }
}

internal fun V0AcurastStorage(engine: RpcEngine, state: State): V0AcurastStorage = V0AcurastStorageImpl(engine, state)

internal open class V0AcurastStorageImpl(private val engine: RpcEngine, private val state: State) : V0AcurastStorage {
    override val version: UInt = V0AcurastStorage.VERSION

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

    override suspend fun getManagerIdentifier(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): Int? {
        val key =
            "AcurastProcessorManager".toByteArray().xxH128() +
                    "ProcessorToManagerIdIndex".toByteArray().xxH128() +
                    accountId.blake2b(128)

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return ByteBuffer.wrap(storage.hexToBa()).readU128().toInt()
    }

    override suspend fun getManagerCounter(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): ULong? {
        val key =
            "AcurastProcessorManager".toByteArray().xxH128() +
                    "ManagerCounter".toByteArray().xxH128() +
                    accountId.blake2b(128)

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return ByteBuffer.wrap(storage.hexToBa()).readU64()
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

        return JobRegistration.read(ByteBuffer.wrap(storage.hexToBa()), version)
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
                jobs.add(JobAssignment.read(change, version))
            }
        }

        return jobs
    }

    private suspend fun getAttestationEncoded(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): String? {
        val key =
            "Acurast".toByteArray().xxH128() +
                    "StoredAttestation".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        val result = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
        )

        return result?.takeIf { it.isNotEmpty() }
    }

    override suspend fun isAttested(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): Boolean =
        try {
            val result = getAttestationEncoded(accountId, blockHash, timeout)

            !result.isNullOrEmpty()
        } catch (e: Throwable) {
            false
        }

    override suspend fun getAttestation(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): Attestation? {
        val storage = getAttestationEncoded(accountId, blockHash, timeout)

        if (storage.isNullOrEmpty()) {
            return null
        }

        return Attestation.read(ByteBuffer.wrap(storage.hexToBa()))
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

    override suspend fun getAsset(managerId: Int, blockHash: ByteArray?, timeout: Long?): PalletUniquesItemDetails? {
        val collectionId = (0).toBigInteger().toU8a()
        val managerId = managerId.toBigInteger().toU8a()

        val key =
            "Uniques".toByteArray().xxH128() +
                    "Asset".toByteArray().xxH128() +
                    collectionId.blake2b(128) + collectionId +
                    managerId.blake2b(128) + managerId

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return PalletUniquesItemDetails.read(ByteBuffer.wrap(storage.hexToBa()))
    }

    override suspend fun getMarketplacePricing(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): MarketplacePricing? {
        val key =
            "AcurastMarketplace".toByteArray().xxH128() +
                    "StoredAdvertisementPricing".toByteArray().xxH128() +
                    accountId.blake2b(128)

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return MarketplacePricing.read(ByteBuffer.wrap(storage.hexToBa()))
    }

    override suspend fun getMarketplaceAdvertisementRestriction(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): MarketplaceAdvertisementRestriction? {
        val key =
            "AcurastMarketplace".toByteArray().xxH128() +
                    "StoredAdvertisementRestriction".toByteArray().xxH128() +
                    accountId.blake2b(128)

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return MarketplaceAdvertisementRestriction.read(ByteBuffer.wrap(storage.hexToBa()))
    }
}