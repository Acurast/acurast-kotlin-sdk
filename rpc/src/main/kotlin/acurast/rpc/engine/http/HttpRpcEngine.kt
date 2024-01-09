package acurast.rpc.engine.http

import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.http.ktor.KtorHttpClient
import acurast.rpc.engine.http.ktor.KtorLogger
import org.json.JSONObject

public class HttpRpcEngine internal constructor(
    private val config: ImmutableHttpRpcEngineConfig,
    private val client: HttpClient,
) : RpcEngine<HttpRpcEngine> {
    override suspend fun request(body: JSONObject, timeout: Long?, peek: Boolean): JSONObject = with(config) {
        val url = if (urls.size == 1) urls.first() else urls.random()
        val body = body.toString()
        val requestTimeout = timeout

        if (peek) peeker?.peek(url, body, headers, parameters, requestTimeout, connectionTimeout)

        val response = client.post(
            url,
            body,
            headers,
            parameters,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout,
        )

        return JSONObject(response)
    }

    override suspend fun <T> contextual(action: suspend (HttpRpcEngine) -> T): T =
        action(HttpRpcEngine(config.copy(urls = listOf(config.urls.random())), client))

    public fun copy(urls: List<String>? = null, client: HttpClient? = null, block: HttpRpcEngineConfig.() -> Unit = {}): HttpRpcEngine =
        HttpRpcEngine(urls ?: config.urls, client ?: this.client) {
            headers = config.headers
            parameters = config.parameters
            connectionTimeout = config.connectionTimeout
            peeker = config.peeker

            block()
        }
}

public interface HttpRpcEngineConfig {
    public val urls: List<String>

    public val headers: List<HttpHeader>?
    public val parameters: List<HttpParameter>?

    public val connectionTimeout: Long?

    public val peeker: HttpRpcEnginePeeker?
}

public data class ImmutableHttpRpcEngineConfig(
    override val urls: List<String>,
    override val headers: List<HttpHeader>?,
    override val parameters: List<HttpParameter>?,
    override val connectionTimeout: Long?,
    override val peeker: HttpRpcEnginePeeker?
) : HttpRpcEngineConfig

public data class MutableHttpRpcEngineConfig(override val urls: List<String>) : HttpRpcEngineConfig {
    override var headers: List<HttpHeader>? = null
    override var parameters: List<HttpParameter>? = null

    override var connectionTimeout: Long? = null

    override var peeker: HttpRpcEnginePeeker? = null
}

public interface HttpRpcEnginePeeker {
    public fun peek(
        url: String,
        body: String,
        headers: List<HttpHeader>?,
        parameters: List<HttpParameter>?,
        requestTimeout: Long?,
        connectionTimeout: Long?,
    )
}

private fun DefaultHttpClient(): HttpClient = KtorHttpClient(object : KtorLogger() {
    override fun log(message: String) {
        println(message)
    }
})

public fun HttpRpcEngine(
    urls: List<String>,
    client: HttpClient = DefaultHttpClient(),
    block: MutableHttpRpcEngineConfig.() -> Unit = {},
): HttpRpcEngine {
    val config = MutableHttpRpcEngineConfig(urls).apply(block).apply {
        headers = (this.headers.orEmpty() + listOf(
            "Content-Type" to "application/json",
            "Accept" to "application/json",
        )).distinctBy { it.first }
    }
    return with(config) {
        HttpRpcEngine(
            ImmutableHttpRpcEngineConfig(urls, headers, parameters, connectionTimeout, peeker),
            client,
        )
    }
}

public fun HttpRpcEngine(
    vararg urls: String,
    client: HttpClient = DefaultHttpClient(),
    block: MutableHttpRpcEngineConfig.() -> Unit = {},
): HttpRpcEngine = HttpRpcEngine(urls.toList(), client, block)