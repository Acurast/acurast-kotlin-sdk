package acurast.rpc.http;

/**
 * A key value HTTP header.
 */
public typealias HttpHeader = Pair<String, String?>

/**
 * A key value HTTP parameter.
 */
public typealias HttpParameter = Pair<String, String?>

/**
 * HTTP client provider interface.
 *
 * Use this interface to register a custom HTTP client implementation.
 * See:
 *  - [KtorHttpClientProvider] for a ready-to-use implementation.
 */
public interface IHttpClientProvider {

    /**
     * Call DELETE HTTP method on specified [baseUrl] with [headers] and [parameters].
     *
     * The method returns a JSON response encoded as [String].
     */
    public suspend fun delete(
        baseUrl: String,
        headers: List<HttpHeader>?,
        parameters: List<HttpParameter>?,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): String

    /**
     * Call GET HTTP method on specified [baseUrl] with [headers] and [parameters].
     *
     * The method returns a JSON response encoded as [String].
     */
    public suspend fun get(
        baseUrl: String,
        headers: List<HttpHeader>? = null,
        parameters: List<HttpParameter>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): String /* raw JSON */

    /**
     * Call PATCH HTTP method on specified [baseUrl] with [headers], [parameters] and [body].
     *
     * The [body] is a serialized JSON in [String] representation.
     * The method returns a JSON response encoded as [String].
     */
    public suspend fun patch(
        baseUrl: String,
        body: String?, /* raw JSON */
        headers: List<HttpHeader>? = null,
        parameters: List<HttpParameter>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): String /* raw JSON */

    /**
     * Call POST HTTP method on specified [baseUrl] with [headers], [parameters] and [body].
     *
     * The [body] is a serialized JSON in [String] representation.
     * The method returns a JSON response encoded as [String].
     */
    public suspend fun post(
        baseUrl: String,
        body: String?, /* raw JSON */
        headers: List<HttpHeader>? = null,
        parameters: List<HttpParameter>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): String /* raw JSON */

    /**
     * Call PUT HTTP method on specified [baseUrl] with [headers], [parameters] and [body].
     *
     * The [body] is a serialized JSON in [String] representation.
     * The method returns a JSON response encoded as [String].
     */
    public suspend fun put(
        baseUrl: String,
        body: String?, /* raw JSON */
        headers: List<HttpHeader>? = null,
        parameters: List<HttpParameter>? = null,
        requestTimeout: Long? = null,
        connectionTimeout: Long? = null,
    ): String /* raw JSON */
}
