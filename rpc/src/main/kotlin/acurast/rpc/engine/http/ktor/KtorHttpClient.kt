package acurast.rpc.engine.http.ktor;

import acurast.rpc.engine.http.HttpClient
import acurast.rpc.engine.http.HttpHeader
import acurast.rpc.engine.http.HttpParameter
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * [KtorHttpClient] implementation that uses [Ktor](https://ktor.io/) to satisfy the interface requirements.
 *
 * @property engineFactory [Ktor HttpClientEngineFactory][HttpClientEngineFactory] that the underlying [Ktor HttpClient][io.ktor.client.HttpClient] should be configured with.
 * @property logger An optional logging configuration.
 */
public class KtorHttpClient(
    private val engineFactory: HttpClientEngineFactory<*> = CIO,
    private val logger: KtorLogger? = null,
): HttpClient {
    private val json: Json by lazy { Json.Default }
    private val ktor: io.ktor.client.HttpClient by lazy {
        io.ktor.client.HttpClient(engineFactory) {
            expectSuccess = true

            install(HttpTimeout)

            install(ContentNegotiation) {
                json(json)
            }

            logger?.let {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            it.log(message)
                        }
                    }
                    level = when (it.level) {
                        KtorLogger.LogLevel.All -> LogLevel.ALL
                        KtorLogger.LogLevel.Headers -> LogLevel.HEADERS
                        KtorLogger.LogLevel.Body -> LogLevel.BODY
                        KtorLogger.LogLevel.Info -> LogLevel.INFO
                        KtorLogger.LogLevel.None -> LogLevel.NONE
                    }
                }
            }
        }
    }

    /**
     * Call DELETE HTTP method on specified [baseUrl] with [headers] and [parameters].
     *
     * The method returns a JSON response encoded as [String].
     */
    override suspend fun delete(
        baseUrl: String,
        headers: List<HttpHeader>?,
        parameters: List<HttpParameter>?,
        requestTimeout: Long?,
        connectionTimeout: Long?,
    ): String = request(HttpMethod.Delete, baseUrl, headers, parameters, requestTimeout, connectionTimeout)

    /**
     * Call GET HTTP method on specified [baseUrl] with [headers] and [parameters].
     *
     * The method returns a JSON response encoded as [String].
     */
    override suspend fun get(
        baseUrl: String,
        headers: List<HttpHeader>?,
        parameters: List<HttpParameter>?,
        requestTimeout: Long?,
        connectionTimeout: Long?,
    ): String = request(HttpMethod.Get, baseUrl, headers, parameters, requestTimeout, connectionTimeout)

    /**
     * Call PATCH HTTP method on specified [baseUrl] with [headers], [parameters] and [body].
     *
     * The [body] is a serialized JSON in [String] representation.
     * The method returns a JSON response encoded as [String].
     */
    override suspend fun patch(
        baseUrl: String,
        body: String?,
        headers: List<HttpHeader>?,
        parameters: List<HttpParameter>?,
        requestTimeout: Long?,
        connectionTimeout: Long?,
    ): String = request(HttpMethod.Patch, baseUrl, headers, parameters, requestTimeout, connectionTimeout) {
        body?.let { setBodyAsText(it) }
    }

    /**
     * Call POST HTTP method on specified [baseUrl] with [headers], [parameters] and [body].
     *
     * The [body] is a serialized JSON in [String] representation.
     * The method returns a JSON response encoded as [String].
     */
    override suspend fun post(
        baseUrl: String,
        body: String?,
        headers: List<HttpHeader>?,
        parameters: List<HttpParameter>?,
        requestTimeout: Long?,
        connectionTimeout: Long?,
    ): String = request(HttpMethod.Post, baseUrl, headers, parameters, requestTimeout, connectionTimeout) {
        body?.let { setBodyAsText(it) }
    }

    /**
     * Call PUT HTTP method on specified [baseUrl] with [headers], [parameters] and [body].
     *
     * The [body] is a serialized JSON in [String] representation.
     * The method returns a JSON response encoded as [String].
     */
    override suspend fun put(
        baseUrl: String,
        body: String?,
        headers: List<HttpHeader>?,
        parameters: List<HttpParameter>?,
        requestTimeout: Long?,
        connectionTimeout: Long?,
    ): String = request(HttpMethod.Put, baseUrl, headers, parameters, requestTimeout, connectionTimeout) {
        body?.let { setBodyAsText(it) }
    }

    private suspend fun request(
        method: HttpMethod,
        baseUrl: String,
        headers: List<HttpHeader>? = emptyList(),
        parameters: List<HttpParameter>? = emptyList(),
        requestTimeout: Long?,
        connectionTimeout: Long?,
        block: HttpRequestBuilder.() -> Unit = {},
    ): String {
        val response = ktor.request {
            this.method = method

            url(baseUrl)

            if (headers != null) {
                headers(headers)
            }
            if (parameters != null) {
                parameters(parameters)
            }

            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)

            timeout {
                requestTimeout?.let { requestTimeoutMillis = it }
                connectionTimeout?.let { connectTimeoutMillis = it }
            }

            block(this)
        }

        return response.bodyAsText()
    }

    private fun HttpRequestBuilder.setBodyAsText(body: String) {
        val jsonElement = json.decodeFromString<JsonElement>(body)
        setBody(jsonElement)
    }

    private fun HttpRequestBuilder.headers(headers: List<HttpHeader>) {
        headers.forEach { header(it.first, it.second) }
    }

    private fun HttpRequestBuilder.parameters(parameters: List<HttpParameter>) {
        parameters.forEach { parameter(it.first, it.second) }
    }
}

/**
 * Creates a new [KtorHttpClient] instance with a default [CIO HttpClientEngineFactory][CIO] and optional [logger].
 */
public fun KtorHttpClient(logger: KtorLogger? = null): KtorHttpClient = KtorHttpClient(CIO, logger)