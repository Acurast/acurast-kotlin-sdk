package acurast.rpc.engine.http

import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.http.ktor.KtorHttpClient
import acurast.rpc.engine.http.ktor.KtorLogger
import org.json.JSONObject

public class HttpRpcEngine internal constructor(
    private val config: HttpRpcEngineConfig,
    private val client: HttpClient,
) : RpcEngine {
    override val id: String
        get() = config.url

    override suspend fun request(body: JSONObject, timeout: Long?): JSONObject = with(config) {
        val body = body.toString()
        val requestTimeout = timeout

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
}
public interface HttpRpcEngineConfig {
    public val url: String
    public val headers: List<HttpHeader>?
    public val parameters: List<HttpParameter>?

    public val connectionTimeout: Long?
}

public data class MutableHttpRpcEngineConfig(override val url: String) : HttpRpcEngineConfig {
    override var headers: List<HttpHeader>? = null
    override var parameters: List<HttpParameter>? = null

    override var connectionTimeout: Long? = null
}

private fun DefaultHttpClient(): HttpClient = KtorHttpClient(object : KtorLogger() {
    override fun log(message: String) {
        println(message)
    }
})

public fun HttpRpcEngine(
    url: String,
    client: HttpClient = DefaultHttpClient(),
    block: MutableHttpRpcEngineConfig.() -> Unit = {},
): HttpRpcEngine {
    val config = MutableHttpRpcEngineConfig(url).apply(block).apply {
        headers = (this.headers.orEmpty() + listOf(
            "Content-Type" to "application/json",
            "Accept" to "application/json",
        )).distinctBy { it.first }
    }

    return HttpRpcEngine(config, client)
}