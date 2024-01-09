package acurast.rpc.engine.http

import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.http.ktor.KtorHttpClient
import acurast.rpc.engine.http.ktor.KtorLogger
import org.json.JSONObject

public class HttpRpcEngine internal constructor(
    private val config: HttpRpcEngineConfig,
    private val client: HttpClient,
) : RpcEngine {
    override suspend fun request(body: JSONObject, timeout: Long?): JSONObject = with(config) {
        val response = client.post(
            url,
            body.toString(),
            headers,
            requestTimeout = timeout,
            connectionTimeout = connectionTimeout,
        )

        return JSONObject(response)
    }
}

public data class HttpRpcEngineConfig(val url: String) {
    var headers: List<HttpHeader>? = null
    var parameters: List<HttpParameter>? = null

    var connectionTimeout: Long? = null
}

public fun HttpRpcEngine(
    url: String,
    client: HttpClient = KtorHttpClient(object : KtorLogger() {
        override fun log(message: String) {
            println(message)
        }
    }),
    block: HttpRpcEngineConfig.() -> Unit,
): HttpRpcEngine {
    val config = HttpRpcEngineConfig(url).apply(block)
    return HttpRpcEngine(config, client)
}