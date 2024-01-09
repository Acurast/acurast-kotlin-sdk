package acurast.rpc.engine.http

import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.http.ktor.KtorHttpClient
import acurast.rpc.engine.http.ktor.KtorLogger
import org.json.JSONObject

public class HttpRpcEngine internal constructor(
    private val config: HttpRpcEngineConfig,
    private val client: HttpClient,
) : RpcEngine {
    override suspend fun request(body: JSONObject, timeout: Long?, peek: Boolean): JSONObject = with(config) {
        val url = urls.random()
        val body = body.toString()
        val requestTimeout = timeout

        if (peek) peeker?.peek(url, body, headers, parameters, requestTimeout, connectionTimeout)

        val response = client.post(
            urls.random(),
            body,
            headers,
            parameters,
            requestTimeout = requestTimeout,
            connectionTimeout = connectionTimeout,
        )

        return JSONObject(response)
    }
}

public data class HttpRpcEngineConfig(val urls: List<String>) {
    var headers: List<HttpHeader>? = null
    var parameters: List<HttpParameter>? = null

    var connectionTimeout: Long? = null

    var peeker: HttpRpcEnginePeeker? = null
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
    block: HttpRpcEngineConfig.() -> Unit = {},
): HttpRpcEngine {
    val config = HttpRpcEngineConfig(urls).apply(block).apply {
        headers = (this.headers.orEmpty() + listOf(
            "Content-Type" to "application/json",
            "Accept" to "application/json",
        )).distinctBy { it.first }
    }
    return HttpRpcEngine(config, client)
}

public fun HttpRpcEngine(
    vararg urls: String,
    client: HttpClient = DefaultHttpClient(),
    block: HttpRpcEngineConfig.() -> Unit = {},
): HttpRpcEngine = HttpRpcEngine(urls.toList(), client, block)