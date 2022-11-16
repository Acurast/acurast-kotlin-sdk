package acurast.rpc

import acurast.codec.extensions.*
import acurast.codec.type.acurast.StoredJobAssignment
import acurast.rpc.pallet.Author
import acurast.rpc.pallet.Chain
import acurast.rpc.pallet.State
import acurast.rpc.type.AccountInfo
import acurast.rpc.type.readAccountInfo
import java.nio.ByteBuffer

public class RPC public constructor(rpc_url: String) {
    public val author: Author = Author(rpc_url)
    public val chain: Chain = Chain(rpc_url)
    public val state: State = State(rpc_url)

    /**
     * Query account information. (nonce, etc...)
     */
    public fun getAccountInfo(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        successCallback: (AccountInfo) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val key =
            "System".toByteArray().xxH128() +
            "Account".toByteArray().xxH128() +
                    accountId.blake2b(128) + accountId;

        state.getStorage(
            storageKey = key,
            blockHash = blockHash,
            successCallback = { storage ->
                successCallback(ByteBuffer.wrap(storage.hexToBa()).readAccountInfo())
            },
            errorCallback = errorCallback
        )
    }

    /**
     * Get all job assignments for a given account.
     */
    public fun getJobAssignments(
        accountId: ByteArray,
        blockHash: ByteArray? = null,
        successCallback: (List<StoredJobAssignment>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val indexKey =
            "AcurastMarketplace".toByteArray().xxH128() +
            "StoredJobAssignment".toByteArray().xxH128() +
            accountId.blake2b(128) + accountId;

        // TODO: Improve code
        state.getKeys(
            key = indexKey,
            blockHash = blockHash,
            successCallback = { keys ->
                val jobs: MutableList<StoredJobAssignment> = mutableListOf()

                if (keys.isEmpty()) {
                    successCallback(jobs)
                } else {
                    for (key in keys) {
                        state.queryStorageAt(
                            storageKey = key.hexToBa(),
                            blockHash = blockHash,
                            successCallback = { storage ->
                                jobs.add(StoredJobAssignment.read(storage[0].changes[0]))
                                successCallback(jobs)
                            },
                            errorCallback = errorCallback
                        )
                    }
                }
            },
            errorCallback = errorCallback
        )
    }
}
