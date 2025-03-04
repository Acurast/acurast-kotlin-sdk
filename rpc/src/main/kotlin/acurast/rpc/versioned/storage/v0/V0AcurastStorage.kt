package acurast.rpc.versioned.storage.v0

import acurast.codec.extensions.*
import acurast.codec.type.MetricPool
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

    public suspend fun getAllJobAssignments(
        jobIdentifier: JobIdentifier,
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

    public suspend fun getMetricPoolIds(
        names: List<String>,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): List<Byte?>

    public suspend fun getMetricPools(
        ids: List<Byte>,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): List<MetricPool?>
}

internal fun V0AcurastStorage(engine: RpcEngine, state: State): V0AcurastStorage = V0AcurastStorageImpl(engine, state)

internal open class V0AcurastStorageImpl(private val engine: RpcEngine, private val state: State) : V0AcurastStorage {
    override val version: UInt = V0AcurastStorage.VERSION

    override suspend fun getAccountInfo(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): FrameSystemAccountInfo {
        val storage = state.getStorage(
            storageKey = System_Account(accountId = accountId),
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
        val storage = state.getStorage(
            storageKey = Assets_Account(assetId = assetId.toU8a(), accountId = accountId),
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
        val storage = state.getStorage(
            storageKey = AcurastProcessorManager_ProcessorToManagerIdIndex(accountId = accountId),
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
        val storage = state.getStorage(
            storageKey = AcurastProcessorManager_ManagerCounter(accountId = accountId),
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
        val storage = state.getStorage(
            storageKey = Acurast_StoredJobRegistration(jobIdentifier = jobIdentifier),
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
        val keys = state.getKeys(
            key = AcurastMarketplace_StoredMatches(accountId = accountId),
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

        return result.getOrNull(0)
            ?.changes
            ?.map { JobAssignment.read(it, version) }
            ?: emptyList()
    }

    override suspend fun getAllJobAssignments(
        jobIdentifier: JobIdentifier,
        blockHash: ByteArray?,
        timeout: Long?
    ): List<JobAssignment> {
        val jobId = jobIdentifier.toU8a()

        val jobIdPartialKey = jobId.blake2b(128) + jobId

        val assignedProcessorsKey = AcurastMarketplace_AssignedProcessors(args = jobIdPartialKey)
        val storedMatchesPartialKey = AcurastMarketplace_StoredMatches()

        val keys = state.getKeys(
            key = AcurastMarketplace_AssignedProcessors(jobIdentifier),
            blockHash,
            timeout,
            engine,
        ).map { (storedMatchesPartialKey + it.hexToBa().drop(assignedProcessorsKey.size) + jobIdPartialKey).toHex() }

        val result = state.queryStorageAt(
            storageKeys = keys,
            blockHash,
            timeout,
            engine,
        )

        return result.getOrNull(0)
            ?.changes
            ?.map { JobAssignment.read(it, version) }
            ?: emptyList()
    }

    private suspend fun getAttestationEncoded(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): String? {
        val result = state.getStorage(
            storageKey = Acurast_StoredAttestation(accountId = accountId),
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
        val storage = state.getStorage(
            storageKey = Acurast_ExecutionEnvironment(jobIdentifier = jobIdentifier, accountId = accountId),
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
        val storage = state.getStorage(
            storageKey = Uniques_Asset(collectionId = 0, managerId = managerId),
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
        val storage = state.getStorage(
            storageKey = AcurastMarketplace_StoredAdvertisementPricing(accountId = accountId),
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
        val storage = state.getStorage(
            storageKey = AcurastMarketplace_StoredAdvertisementRestriction(accountId = accountId),
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return MarketplaceAdvertisementRestriction.read(ByteBuffer.wrap(storage.hexToBa()))
    }

    private fun ByteArray.drop(n: Int): ByteArray = sliceArray(n..<size)

    override suspend fun getMetricPoolIds(names: List<String>, blockHash: ByteArray?, timeout: Long?): List<Byte?> {
        val prefix = AcurastCompute_MetricPoolLookup()
        val keys = names.map { (prefix + it.toByteArray().copyOf(24)).toHex() }

        val storage = state.queryStorageAt(
            storageKeys = keys,
            blockHash,
            timeout,
            engine,
        )

        return storage.getOrNull(0)
            ?.changes
            ?.map { c -> c.getOrNull(1)?.let { ByteBuffer.wrap(it.hexToBa()).readU8().toByte() } }
            ?: emptyList()
    }

    override suspend fun getMetricPools(ids: List<Byte>, blockHash: ByteArray?, timeout: Long?): List<MetricPool?> {
        val prefix = AcurastCompute_MetricPools()
        val keys = ids.map { (prefix + it).toHex() }

        val storage = state.queryStorageAt(
            storageKeys = keys,
            blockHash,
            timeout,
            engine,
        )

        return storage.getOrNull(0)
            ?.changes
            ?.map { c -> c.getOrNull(1)?.let { MetricPool.read(ByteBuffer.wrap(it.hexToBa())) } }
            ?: emptyList()
    }

    companion object {
        private fun Acurast_StoredAttestation(accountId: ByteArray): ByteArray =
            "Acurast".toByteArray().xxH128() +
                    "StoredAttestation".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        private fun Acurast_StoredJobRegistration(jobIdentifier: JobIdentifier): ByteArray {
            val origin = jobIdentifier.origin.toU8a()
            val jobId = jobIdentifier.id.toU8a()

            return "Acurast".toByteArray().xxH128() +
                    "StoredJobRegistration".toByteArray().xxH128() +
                    origin.blake2b(128) + origin +
                    jobId.blake2b(128) + jobId
        }

        private fun Acurast_ExecutionEnvironment(jobIdentifier: JobIdentifier, accountId: ByteArray): ByteArray {
            val jobIdentifier = jobIdentifier.origin.toU8a() + jobIdentifier.id.toU8a()

            return "Acurast".toByteArray().xxH128() +
                    "ExecutionEnvironment".toByteArray().xxH128() +
                    jobIdentifier.blake2b(128) + jobIdentifier +
                    accountId.blake2b(128) + accountId
        }

        private fun AcurastProcessorManager_ProcessorToManagerIdIndex(accountId: ByteArray): ByteArray =
            "AcurastProcessorManager".toByteArray().xxH128() +
                    "ProcessorToManagerIdIndex".toByteArray().xxH128() +
                    accountId.blake2b(128)

        private fun AcurastProcessorManager_ManagerCounter(accountId: ByteArray): ByteArray =
            "AcurastProcessorManager".toByteArray().xxH128() +
                    "ManagerCounter".toByteArray().xxH128() +
                    accountId.blake2b(128)

        private fun AcurastMarketplace_StoredMatches(accountId: ByteArray, jobIdentifier: JobIdentifier? = null): ByteArray {
            val jobIdentifier = jobIdentifier?.toU8a()

            return AcurastMarketplace_StoredMatches(
                accountId.blake2b(128) + accountId +
                        (jobIdentifier?.let { it.blake2b(128) + it } ?: byteArrayOf())
            )
        }

        private fun AcurastMarketplace_StoredMatches(args: ByteArray = byteArrayOf()): ByteArray =
            "AcurastMarketplace".toByteArray().xxH128() +
                    "StoredMatches".toByteArray().xxH128() + args

        private fun AcurastMarketplace_AssignedProcessors(jobIdentifier: JobIdentifier, accountId: ByteArray? = null): ByteArray {
            val jobIdentifier = jobIdentifier.toU8a()

            return AcurastMarketplace_AssignedProcessors(
                jobIdentifier.blake2b(128) + jobIdentifier +
                        (accountId?.let { it.blake2b(128) + it } ?: byteArrayOf())
            )
        }

        private fun AcurastMarketplace_AssignedProcessors(args: ByteArray = byteArrayOf()): ByteArray =
            "AcurastMarketplace".toByteArray().xxH128() +
                    "AssignedProcessors".toByteArray().xxH128() + args

        private fun AcurastMarketplace_StoredAdvertisementPricing(accountId: ByteArray): ByteArray =
            "AcurastMarketplace".toByteArray().xxH128() +
                    "StoredAdvertisementPricing".toByteArray().xxH128() +
                    accountId.blake2b(128)

        private fun AcurastMarketplace_StoredAdvertisementRestriction(accountId: ByteArray): ByteArray =
            "AcurastMarketplace".toByteArray().xxH128() +
                    "StoredAdvertisementRestriction".toByteArray().xxH128() +
                    accountId.blake2b(128)

        private fun System_Account(accountId: ByteArray): ByteArray =
            "System".toByteArray().xxH128() +
                    "Account".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId

        private fun Assets_Account(assetId: ByteArray, accountId: ByteArray): ByteArray =
            "Assets".toByteArray().xxH128() +
                    "Account".toByteArray().xxH128() +
                    assetId.blake2b(128) + assetId +
                    accountId.blake2b(128) + accountId

        private fun Uniques_Asset(collectionId: Int, managerId: Int): ByteArray {
            val collectionId = collectionId.toBigInteger().toU8a()
            val managerId = managerId.toBigInteger().toU8a()

            return "Uniques".toByteArray().xxH128() +
                    "Asset".toByteArray().xxH128() +
                    collectionId.blake2b(128) + collectionId +
                    managerId.blake2b(128) + managerId
        }

        private fun AcurastCompute_MetricPoolLookup(name: String): ByteArray =
            AcurastCompute_MetricPoolLookup(name.toByteArray().copyOf(24))

        private fun AcurastCompute_MetricPoolLookup(args: ByteArray = byteArrayOf()): ByteArray =
            "AcurastCompute".toByteArray().xxH128() +
                    "MetricPoolLookup".toByteArray().xxH128() + args

        private fun AcurastCompute_MetricPools(id: Byte): ByteArray =
            AcurastCompute_MetricPoolLookup(byteArrayOf(id))

        private fun AcurastCompute_MetricPools(args: ByteArray = byteArrayOf()): ByteArray =
            "AcurastCompute".toByteArray().xxH128() +
                    "MetricPools".toByteArray().xxH128() + args
    }
}