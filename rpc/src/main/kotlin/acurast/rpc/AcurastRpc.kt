package acurast.rpc

import acurast.codec.extensions.xxH128
import acurast.rpc.engine.RpcEngine
import acurast.rpc.pallet.Author
import acurast.rpc.pallet.Chain
import acurast.rpc.pallet.State
import acurast.rpc.versioned.VersionedAcurastRpc
import acurast.rpc.versioned.v0.V0AcurastRpc
import acurast.rpc.versioned.v0.compat

public interface AcurastRpc {
    public val engine: RpcEngine

    public suspend fun getApiVersion(blockHash: ByteArray? = null, timeout: Long? = null): UInt
    public fun api(version: UInt): VersionedAcurastRpc
}

public fun AcurastRpc(engine: RpcEngine): AcurastRpc {
    val author = Author()
    val chain = Chain()
    val state = State()

    return AcurastRpcImpl(
        engine,
        state,
        versioned = listOf(
            V0AcurastRpc(engine, author, chain, state).compat(),
        ).associateBy { it.version },
    )
}

private class AcurastRpcImpl(
    override val engine: RpcEngine,
    private val state: State,
    private val versioned: Map<UInt, VersionedAcurastRpc>,
) : AcurastRpc {
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

    override fun api(version: UInt): VersionedAcurastRpc = versioned[version] ?: failWithUnsupportedApiVersion(version)

    private fun failWithUnsupportedApiVersion(version: UInt): Nothing =
        throw RuntimeException("Acurast RPC API version $version is not supported.")
}