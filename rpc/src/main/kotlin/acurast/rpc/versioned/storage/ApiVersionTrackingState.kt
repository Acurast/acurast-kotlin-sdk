package acurast.rpc.versioned.storage

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.readU32
import acurast.codec.extensions.toHex
import acurast.rpc.AcurastProcessorManager_ApiVersion
import acurast.rpc.engine.RpcEngine
import acurast.rpc.pallet.State
import acurast.rpc.type.StorageQueryResult
import java.nio.ByteBuffer

/**
 * A [State] that reads the storage API version along with every storage read, in the same request and so
 * at the same block, and reports it to [onApiVersion]. The results it returns are those of the keys asked for.
 */
internal class ApiVersionTrackingState(private val onApiVersion: (UInt) -> Unit) : State() {
    private val apiVersionKey: String = AcurastProcessorManager_ApiVersion().toHex()

    override suspend fun getStorage(storageKey: ByteArray, blockHash: ByteArray?, timeout: Long?, engine: RpcEngine): String? {
        val key = storageKey.toHex()
        val changes = queryStorageAt(listOf(key), blockHash, timeout, engine).firstOrNull()?.changes.orEmpty()

        return changes.firstOrNull { it.keyEquals(key) }?.getOrNull(1)
    }

    override suspend fun queryStorageAt(
        storageKeys: List<String>,
        blockHash: ByteArray?,
        timeout: Long?,
        engine: RpcEngine,
    ): List<StorageQueryResult> {
        val results = super.queryStorageAt(storageKeys + apiVersionKey, blockHash, timeout, engine)

        return results.map { result ->
            val (apiVersionChanges, changes) = result.changes.partition { it.keyEquals(apiVersionKey) }
            apiVersionChanges.firstOrNull()?.let { onApiVersion(it.apiVersion()) }

            result.copy(changes = changes)
        }
    }

    // Keys go out without the `0x` prefix the node returns them with.
    private fun List<String>.keyEquals(key: String): Boolean =
        firstOrNull()?.removePrefix("0x").equals(key.removePrefix("0x"), ignoreCase = true)

    /** Read as [acurast.rpc.AcurastRpc.getApiVersion] does: no value is version 0. */
    private fun List<String>.apiVersion(): UInt =
        getOrNull(1)?.takeIf { it.isNotEmpty() }?.let { ByteBuffer.wrap(it.hexToBa()).readU32() } ?: 0u
}
