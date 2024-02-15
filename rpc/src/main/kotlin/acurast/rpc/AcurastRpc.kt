package acurast.rpc

import acurast.codec.extensions.xxH128
import acurast.rpc.engine.RpcEngine
import acurast.rpc.pallet.Author
import acurast.rpc.pallet.Chain
import acurast.rpc.pallet.RuntimeVersion
import acurast.rpc.pallet.State
import acurast.rpc.type.Header
import acurast.rpc.versioned.storage.AcurastStorage
import acurast.rpc.versioned.storage.v0.V0AcurastStorage
import acurast.rpc.versioned.storage.v0.compat
import java.math.BigInteger

public interface AcurastRpc {
    public val engine: RpcEngine

    public suspend fun getBlockHash(blockNumber: BigInteger? = null, timeout: Long? = null): String?
    public suspend fun getHeader(blockHash: ByteArray? = null, timeout: Long? = null): Header

    public suspend fun getApiVersion(blockHash: ByteArray? = null, timeout: Long? = null): UInt
    public suspend fun getRuntimeVersion(blockHash: ByteArray? = null, timeout: Long? = null): RuntimeVersion

    public suspend fun submitExtrinsic(extrinsic: ByteArray, timeout: Long? = null): String?
    public suspend fun call(method: String, data: ByteArray? = null, blockHash: ByteArray? = null, timeout: Long? = null): String?

    public fun storage(version: UInt): AcurastStorage
}

public fun AcurastRpc(engine: RpcEngine): AcurastRpc {
    val author = Author()
    val chain = Chain()
    val state = State()

    return AcurastRpcImpl(
        engine,
        state,
        author,
        chain,
        storages = listOf(
            V0AcurastStorage(engine, state).compat(),
        ).associateBy { it.version },
    )
}

private class AcurastRpcImpl(
    override val engine: RpcEngine,
    private val state: State,
    private val author: Author,
    private val chain: Chain,
    private val storages: Map<UInt, AcurastStorage>,
) : AcurastRpc {

    override suspend fun getBlockHash(blockNumber: BigInteger?, timeout: Long?): String? =
        chain.getBlockHash(blockNumber, timeout, engine)

    override suspend fun getHeader(blockHash: ByteArray?, timeout: Long?): Header =
        chain.getHeader(blockHash, timeout, engine)

    override suspend fun getApiVersion(blockHash: ByteArray?, timeout: Long?): UInt {
        val key =
            "AcurastProcessorManager".toByteArray().xxH128() +
                    "ApiVersion".toByteArray().xxH128()

        val storage = state.getStorage(
            storageKey = key,
            blockHash,
            timeout,
            engine,
        )

        return storage?.toUIntOrNull() ?: 0u
    }

    override suspend fun getRuntimeVersion(blockHash: ByteArray?, timeout: Long?): RuntimeVersion =
        state.getRuntimeVersion(blockHash, timeout, engine)

    override suspend fun submitExtrinsic(extrinsic: ByteArray, timeout: Long?): String? =
        author.submitExtrinsic(extrinsic, timeout, engine)

    override suspend fun call(method: String, data: ByteArray?, blockHash: ByteArray?, timeout: Long?): String? =
        state.call(method, data, blockHash, timeout, engine)

    override fun storage(version: UInt): AcurastStorage = storages[version] ?: failWithUnsupportedApiVersion(version)

    private fun failWithUnsupportedApiVersion(version: UInt): Nothing =
        throw RuntimeException("Acurast RPC API version $version is not supported.")
}