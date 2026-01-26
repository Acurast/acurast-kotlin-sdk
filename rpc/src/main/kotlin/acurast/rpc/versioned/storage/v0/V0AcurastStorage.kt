package acurast.rpc.versioned.storage.v0

import acurast.codec.extensions.*
import acurast.codec.type.*
import acurast.codec.type.acurast.*
import acurast.codec.type.compute.Commitment
import acurast.codec.type.compute.Delegation
import acurast.codec.type.compute.MetricPool
import acurast.codec.type.manager.ProcessorPairing
import acurast.codec.type.manager.ProcessorUpdateInfo
import acurast.codec.type.marketplace.JobAssignment
import acurast.codec.type.tokenconversion.TokenConversion
import acurast.codec.type.uniques.PalletUniquesItemDetails
import acurast.codec.type.vesting.Vesting
import acurast.rpc.AcurastProcessorManager_ProcessorUpdateInfo
import acurast.rpc.engine.RpcEngine
import acurast.rpc.pallet.State
import acurast.rpc.utils.readChangeKeyOrNull
import acurast.rpc.utils.readChangeValueOrNull
import acurast.rpc.versioned.storage.VersionedAcurastStorage
import java.math.BigInteger
import java.nio.ByteBuffer

public interface V0AcurastStorage : VersionedAcurastStorage {
    public suspend fun getAccountInfo(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): AccountInfo

    public suspend fun getAccountOverview(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): AccountOverview?

    /**
     * Get the manager paired with this device
     */
    public suspend fun getManagerIndexForProcessor(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): BigInteger?

    public suspend fun getManagerCounter(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): ULong?
    
    public suspend fun getManagerIndex(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): BigInteger?

    /**
     * Get the registration information of a given job.
     */
    public suspend fun getJobRegistration(
        jobIdentifier: JobIdentifier,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): JobRegistration?

    public suspend fun getJobData(
        jobIdentifier: JobIdentifier,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): JobData?

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

    public suspend fun getAsset(managerId: BigInteger, blockHash: ByteArray? = null, timeout: Long? = null): PalletUniquesItemDetails?

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

    public suspend fun getManagementData(
        managerId: BigInteger?,
        managerAccountId: ByteArray?,
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null,
    ): ManagementData

    public suspend fun getProcessorOverview(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        timeout: Long? = null
    ): ProcessorOverview
}

internal fun V0AcurastStorage(engine: RpcEngine, state: State): V0AcurastStorage = V0AcurastStorageImpl(engine, state)

internal open class V0AcurastStorageImpl(private val engine: RpcEngine, private val state: State) : V0AcurastStorage {
    override val version: UInt = V0AcurastStorage.VERSION

    override suspend fun getAccountInfo(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): AccountInfo {
        val storage = state.getStorage(
            storageKey = System_Account(accountId = accountId),
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return AccountInfo()
        }

        return AccountInfo.read(ByteBuffer.wrap(storage.hexToBa()))
    }

    override suspend fun getAccountOverview(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?
    ): AccountOverview? {
        val committerIdPartialKey = Uniques_Account(accountId = accountId, collectionId = 1)
        val committerIdKey = state.getKeys(
            key = committerIdPartialKey,
            blockHash,
            timeout,
            engine,
        ).firstOrNull()

        val delegationPartialKey = AcurastCompute_Delegations(accountId = accountId)
        val delegationKeys = state.getKeys(
            key = delegationPartialKey,
            blockHash,
            timeout,
            engine,
        )
        val systemAccountKey = System_Account(accountId = accountId)
        val vestingKey = Vesting_Vesting(accountId = accountId)
        val tokenConversionKey = AcurastTokenConversion_LockedConversion(accountId = accountId)

        val storageFirst = state.queryStorageAt(
            storageKeys = listOfNotNull(
                systemAccountKey.toHex(),
                vestingKey.toHex(),
                tokenConversionKey.toHex(),
                committerIdKey,
            ) + delegationKeys,
            blockHash,
            timeout,
            engine,
        )

        val changesFirst = storageFirst.getOrNull(0)?.changes ?: return null
        val accountInfo = changesFirst.readChangeValueOrNull(0) { AccountInfo.read(it) } ?: return null
        val vesting = changesFirst.readChangeValueOrNull(1) { it.readList { Vesting.read(this) } } ?: emptyList()
        val tokenConversion = changesFirst.readChangeValueOrNull(2) { TokenConversion.read(it) }
        val committerId = committerIdKey?.let {
            changesFirst.readChangeKeyOrNull(3) {
                it.readByteArray(committerIdPartialKey.size + 16 /* blake2_128(u128).size */)
                it.readU128()
            }
        }
        val delegations = changesFirst.subList(if (committerIdKey != null) 4 else 3, changesFirst.size).mapNotNull { delegationChange ->
            val committerId = delegationChange.readChangeKeyOrNull { it.positionRelative(-16).readU128() } ?: return@mapNotNull null
            val delegation = delegationChange.readChangeValueOrNull { Delegation.read(it) } ?: return@mapNotNull null

            committerId to delegation
        }

        val ownCommitmentKey = committerId?.let { AcurastCompute_Commitments(it) }
        val delegationCommitmentKeys = delegations.map { AcurastCompute_Commitments(it.first)}

        val storageSecond = state.queryStorageAt(
            storageKeys = listOfNotNull(ownCommitmentKey?.toHex()) + delegationCommitmentKeys.map { it.toHex() },
            blockHash,
            timeout,
            engine,
        )

        val changesSecond = storageSecond.getOrNull(0)?.changes
        val commitments = changesSecond?.mapNotNull { change ->
            val committerId = change.readChangeKeyOrNull { it.positionRelative(-16).readU128() } ?: return@mapNotNull null
            val commitment = change.readChangeValueOrNull { Commitment.read(it) } ?: return@mapNotNull null

            committerId to commitment
        }?.toMap() ?: emptyMap()

        return AccountOverview(
            accountInfo = accountInfo,
            commitment = committerId?.let { commitments[it] },
            delegations = delegations.mapNotNull { (committerId, delegation) ->
                val commitment = commitments[committerId] ?: return@mapNotNull null

                delegation to commitment
           },
            vesting = vesting,
            conversion = tokenConversion,
        )
    }

    override suspend fun getManagerIndexForProcessor(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?,
    ): BigInteger? {
        val storage = state.getStorage(
            storageKey = AcurastProcessorManager_ProcessorToManagerIdIndex(accountId = accountId),
            blockHash,
            timeout,
            engine,
        )

        if (storage.isNullOrEmpty()) {
            return null
        }

        return ByteBuffer.wrap(storage.hexToBa()).readU128()
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

    override suspend fun getManagerIndex(accountId: ByteArray, blockHash: ByteArray?, timeout: Long?): BigInteger? {
        val partialKey = Uniques_Account(accountId, 0)
        val keys = state.getKeys(
            key = partialKey,
            blockHash,
            timeout,
            engine,
        )

        return keys
            .firstOrNull()
            ?.let {
                val bytes = ByteBuffer.wrap(it.hexToBa())
                bytes.readByteArray(partialKey.size + 16 /* blake2_128(u128).size */)

                bytes.readU128()
            }
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

    override suspend fun getJobData(jobIdentifier: JobIdentifier, blockHash: ByteArray?, timeout: Long?): JobData? {
        val registrationKey = Acurast_StoredJobRegistration(jobIdentifier = jobIdentifier)
        val keyIdKey = AcurastMarketplace_JobKeyIds(jobIdentifier = jobIdentifier)

        val storage = state.queryStorageAt(
            storageKeys = listOf(registrationKey.toHex(), keyIdKey.toHex()),
            blockHash,
            timeout,
            engine,
        )

        val changes = storage.getOrNull(0)?.changes ?: return null
        val jobRegistration = changes.readChangeValueOrNull(0) { JobRegistration.read(it, apiVersion = version) } ?: return null
        val keyId = changes.readChangeValueOrNull(1) { it.readByteArray(32) }

        return JobData(jobRegistration, keyId)
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

    override suspend fun getAsset(managerId: BigInteger, blockHash: ByteArray?, timeout: Long?): PalletUniquesItemDetails? {
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
            ?.map { c -> c.readChangeValueOrNull { it.readU8().toByte() } }
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
            ?.map { c -> c.readChangeValueOrNull { MetricPool.read(it) } }
            ?: emptyList()
    }

    override suspend fun getManagementData(
        managerId: BigInteger?,
        managerAccountId: ByteArray?,
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?
    ): ManagementData {
        val updateInfoKey = AcurastProcessorManager_ProcessorUpdateInfo(accountId)
        val managementEndpointKey = managerId?.let { AcurastProcessorManager_ManagementEndpoint(it) }
        val processorMigrationDataKey = managerAccountId?.let { AcurastProcessorManager_ProcessorMigrationData(it) }

        val storage = state.queryStorageAt(
            storageKeys = listOfNotNull(
                updateInfoKey.toHex(),
                managementEndpointKey?.toHex(),
                processorMigrationDataKey?.toHex(),
            ),
            blockHash,
            timeout,
            engine,
        )

        val changes = storage.getOrNull(0)?.changes ?: return ManagementData()
        val updateInfo = changes.readChangeValueOrNull(0) { ProcessorUpdateInfo.read(it) }
        val managementEndpoint = changes.readChangeValueOrNull(1) { it.readString() }
        val processorMigrationData = changes.readChangeValueOrNull(2) { ProcessorPairing.Proof.read(it) }

        return ManagementData(updateInfo, managementEndpoint, processorMigrationData)
    }

    override suspend fun getProcessorOverview(
        accountId: ByteArray,
        blockHash: ByteArray?,
        timeout: Long?
    ): ProcessorOverview {
        val managerIdKey = AcurastProcessorManager_ProcessorToManagerIdIndex(accountId)
        val attestationKey = Acurast_StoredAttestation(accountId)
        val heartbeatKey = AcurastProcessorManager_ProcessorHeartbeat(accountId)

        val storage = state.queryStorageAt(
            storageKeys = listOf(
                managerIdKey.toHex(),
                attestationKey.toHex(),
                heartbeatKey.toHex(),
            ),
            blockHash,
            timeout,
            engine,
        )

        val changes = storage.getOrNull(0)?.changes ?: return ProcessorOverview()
        val managerId = changes.readChangeValueOrNull(0) { it.readU128() }
        val isAttested = changes.readChangeValueOrNull(1) { it } != null
        val lastHeartbeat = changes.readChangeValueOrNull(2) { it.readU128() }

        return ProcessorOverview(managerId, isAttested, lastHeartbeat)
    }
}

private const val PALLET_ACURAST = "Acurast"
private const val PALLET_ACURAST_PROCESSOR_MANAGER = "AcurastProcessorManager"
private const val PALLET_ACURAST_MARKETPLACE = "AcurastMarketplace"
private const val PALLET_ACURAST_COMPUTE = "AcurastCompute"
private const val PALLET_ACURAST_TOKEN_CONVERSION = "AcurastTokenConversion"
private const val PALLET_SYSTEM = "System"
private const val PALLET_UNIQUES = "Uniques"
private const val PALLET_VESTING = "Vesting"

internal fun Acurast_StoredAttestation(accountId: ByteArray): ByteArray =
    PALLET_ACURAST.toByteArray().xxH128() +
            "StoredAttestation".toByteArray().xxH128() +
            accountId.blake2b(128) + accountId

internal fun Acurast_StoredJobRegistration(jobIdentifier: JobIdentifier): ByteArray {
    val origin = jobIdentifier.origin.toU8a()
    val jobId = jobIdentifier.id.toU8a()

    return PALLET_ACURAST.toByteArray().xxH128() +
            "StoredJobRegistration".toByteArray().xxH128() +
            origin.blake2b(128) + origin +
            jobId.blake2b(128) + jobId
}

internal fun Acurast_ExecutionEnvironment(jobIdentifier: JobIdentifier, accountId: ByteArray): ByteArray {
    val jobIdentifier = jobIdentifier.origin.toU8a() + jobIdentifier.id.toU8a()

    return PALLET_ACURAST.toByteArray().xxH128() +
            "ExecutionEnvironment".toByteArray().xxH128() +
            jobIdentifier.blake2b(128) + jobIdentifier +
            accountId.blake2b(128) + accountId
}

internal fun AcurastProcessorManager_ProcessorToManagerIdIndex(accountId: ByteArray): ByteArray =
    PALLET_ACURAST_PROCESSOR_MANAGER.toByteArray().xxH128() +
            "ProcessorToManagerIdIndex".toByteArray().xxH128() +
            accountId.blake2b(128)

internal fun AcurastProcessorManager_ManagerCounter(accountId: ByteArray): ByteArray =
    PALLET_ACURAST_PROCESSOR_MANAGER.toByteArray().xxH128() +
            "ManagerCounter".toByteArray().xxH128() +
            accountId.blake2b(128)

internal fun AcurastProcessorManager_ManagementEndpoint(managerId: BigInteger): ByteArray {
    val managerId = managerId.toU8a()

    return PALLET_ACURAST_PROCESSOR_MANAGER.toByteArray().xxH128() +
            "ManagementEndpoint".toByteArray().xxH128() +
            managerId.blake2b(128) + managerId
}

internal fun AcurastProcessorManager_ProcessorMigrationData(accountId: ByteArray): ByteArray =
    PALLET_ACURAST_PROCESSOR_MANAGER.toByteArray().xxH128() +
            "ProcessorMigrationData".toByteArray().xxH128() +
            accountId.blake2b(128) + accountId

internal fun AcurastProcessorManager_ProcessorHeartbeat(accountId: ByteArray): ByteArray =
    PALLET_ACURAST_PROCESSOR_MANAGER.toByteArray().xxH128() +
            "ProcessorHeartbeat".toByteArray().xxH128() +
            accountId.blake2b(128)

internal fun AcurastMarketplace_StoredMatches(accountId: ByteArray, jobIdentifier: JobIdentifier? = null): ByteArray {
    val jobIdentifier = jobIdentifier?.toU8a()

    return AcurastMarketplace_StoredMatches(
        accountId.blake2b(128) + accountId +
                (jobIdentifier?.let { it.blake2b(128) + it } ?: byteArrayOf())
    )
}

private fun AcurastMarketplace_StoredMatches(args: ByteArray = byteArrayOf()): ByteArray =
    PALLET_ACURAST_MARKETPLACE.toByteArray().xxH128() +
            "StoredMatches".toByteArray().xxH128() + args

internal fun AcurastMarketplace_JobKeyIds(jobIdentifier: JobIdentifier): ByteArray {
    val jobIdentifier = jobIdentifier.toU8a()

    return PALLET_ACURAST_MARKETPLACE.toByteArray().xxH128() +
            "JobKeyIds".toByteArray().xxH128() +
            jobIdentifier.blake2b(128) + jobIdentifier
}

internal fun AcurastMarketplace_AssignedProcessors(jobIdentifier: JobIdentifier, accountId: ByteArray? = null): ByteArray {
    val jobIdentifier = jobIdentifier.toU8a()

    return AcurastMarketplace_AssignedProcessors(
        jobIdentifier.blake2b(128) + jobIdentifier +
                (accountId?.let { it.blake2b(128) + it } ?: byteArrayOf())
    )
}

private fun AcurastMarketplace_AssignedProcessors(args: ByteArray = byteArrayOf()): ByteArray =
    PALLET_ACURAST_MARKETPLACE.toByteArray().xxH128() +
            "AssignedProcessors".toByteArray().xxH128() + args

internal fun AcurastMarketplace_StoredAdvertisementPricing(accountId: ByteArray): ByteArray =
    PALLET_ACURAST_MARKETPLACE.toByteArray().xxH128() +
            "StoredAdvertisementPricing".toByteArray().xxH128() +
            accountId.blake2b(128)

internal fun AcurastMarketplace_StoredAdvertisementRestriction(accountId: ByteArray): ByteArray =
    PALLET_ACURAST_MARKETPLACE.toByteArray().xxH128() +
            "StoredAdvertisementRestriction".toByteArray().xxH128() +
            accountId.blake2b(128)

internal fun System_Account(accountId: ByteArray): ByteArray =
    PALLET_SYSTEM.toByteArray().xxH128() +
            "Account".toByteArray().xxH128() +
            accountId.blake2b(128) + accountId

internal fun Uniques_Account(accountId: ByteArray, collectionId: Int): ByteArray {
    val collectionId = collectionId.toBigInteger().toU8a()

    return PALLET_UNIQUES.toByteArray().xxH128() +
            "Account".toByteArray().xxH128() +
            accountId.blake2b(128) + accountId +
            collectionId.blake2b(128) + collectionId
}

internal fun Uniques_Asset(collectionId: Int, managerId: BigInteger): ByteArray {
    val collectionId = collectionId.toBigInteger().toU8a()
    val managerId = managerId.toU8a()

    return PALLET_UNIQUES.toByteArray().xxH128() +
            "Asset".toByteArray().xxH128() +
            collectionId.blake2b(128) + collectionId +
            managerId.blake2b(128) + managerId
}

internal fun AcurastCompute_Commitments(committerId: BigInteger): ByteArray =
    PALLET_ACURAST_COMPUTE.toByteArray().xxH128() +
            "Commitments".toByteArray().xxH128() +
            committerId.toU8a()

internal fun AcurastCompute_Delegations(accountId: ByteArray, committerId: BigInteger? = null): ByteArray =
    PALLET_ACURAST_COMPUTE.toByteArray().xxH128() +
            "Delegations".toByteArray().xxH128() +
            accountId.xxH64() + accountId +
            (committerId?.toU8a() ?: byteArrayOf())

internal fun AcurastCompute_MetricPoolLookup(name: String): ByteArray =
    AcurastCompute_MetricPoolLookup(name.toByteArray().copyOf(24))

internal fun AcurastCompute_MetricPoolLookup(args: ByteArray = byteArrayOf()): ByteArray =
    PALLET_ACURAST_COMPUTE.toByteArray().xxH128() +
            "MetricPoolLookup".toByteArray().xxH128() + args

internal fun AcurastCompute_MetricPools(id: Byte): ByteArray =
    AcurastCompute_MetricPools(byteArrayOf(id))

internal fun AcurastCompute_MetricPools(args: ByteArray = byteArrayOf()): ByteArray =
    PALLET_ACURAST_COMPUTE.toByteArray().xxH128() +
            "MetricPools".toByteArray().xxH128() + args

internal fun AcurastTokenConversion_LockedConversion(accountId: ByteArray): ByteArray =
    PALLET_ACURAST_TOKEN_CONVERSION.toByteArray().xxH128() +
            "LockedConversion".toByteArray().xxH128() +
            accountId.blake2b(128) + accountId

internal fun Vesting_Vesting(accountId: ByteArray): ByteArray =
    PALLET_VESTING.toByteArray().xxH128() +
            "Vesting".toByteArray().xxH128() +
            accountId.blake2b(128) + accountId