package org.structured_barebone.data.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.structured_barebone.data.model.ProblemDetails
import org.structured_barebone.data.model.RefreshRequest
import org.structured_barebone.data.model.ServerException
import org.structured_barebone.data.model.TokenPair

internal const val STATUS_UPGRADE_REQUIRED = 426

internal val apiJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
    encodeDefaults = true
}

/** Only these methods are repeated after a transient failure; a repeated POST could duplicate its effect. */
private val idempotentMethods = setOf(HttpMethod.Get, HttpMethod.Head, HttpMethod.Options, HttpMethod.Put, HttpMethod.Delete)

/** JSON, timeouts and the client version header. Also used by the plain client that performs the token refresh. */
fun HttpClientConfig<*>.installBasePlugins(options: HttpClientOptions) {
    install(ContentNegotiation) {
        json(apiJson)
    }
    install(HttpTimeout) {
        connectTimeoutMillis = options.connectTimeoutMillis
        socketTimeoutMillis = options.socketTimeoutMillis
        requestTimeoutMillis = options.requestTimeoutMillis
    }
    install(DefaultRequest) {
        header(HttpDefaults.VERSION_HEADER, AppInfo.headerVersion())
    }
}

/**
 * Full plugin set: base plugins, retry, bearer authentication with refresh, logging and the response
 * validator that turns problem documents into [ServerException].
 *
 * @param refreshClient builds a client WITHOUT the Auth plugin. The refresh request must not go through
 * the authenticated client: a 401 on the refresh call would trigger another refresh and deadlock.
 */
fun HttpClientConfig<*>.installCommonPlugins(options: HttpClientOptions, refreshClient: () -> HttpClient) {
    installBasePlugins(options)

    install(HttpRequestRetry) {
        maxRetries = options.maxRetries
        retryIf { request, response -> request.method in idempotentMethods && response.status.value in 502..504 }
        retryOnExceptionIf { request, cause ->
            request.method in idempotentMethods && (
                cause is IOException || cause is SocketTimeoutException ||
                    cause is ConnectTimeoutException || cause is HttpRequestTimeoutException
                )
        }
        exponentialDelay(base = 2.0, baseDelayMs = options.retryBaseDelayMillis, maxDelayMs = HttpDefaults.RETRY_MAX_DELAY_MS, randomizationMs = options.retryJitterMillis)
    }

    HttpResponseValidator {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                if (response.status.value == STATUS_UPGRADE_REQUIRED) options.updateGate?.markRequired()
                throw ServerException(response.status.value, parseProblem(response.bodyAsText()))
            }
        }
    }


    val tokenProvider = options.tokenProvider
    if (tokenProvider != null) {
        val refreshUrl = options.refreshUrl
        val trustedHost = refreshUrl?.let { Url(it).host }
        val plainClient by lazy(refreshClient)
        install(Auth) {
            bearer {
                loadTokens {
                    tokenProvider.accessToken()?.let { BearerTokens(it, tokenProvider.refreshToken()) }
                }
                refreshTokens {
                    val refreshToken = tokenProvider.refreshToken() ?: return@refreshTokens null
                    if (refreshUrl == null) return@refreshTokens null
                    withTimeoutOrNull(HttpDefaults.REFRESH_TIMEOUT_MS) {
                        try {
                            val response = plainClient.post(refreshUrl) {
                                contentType(ContentType.Application.Json)
                                setBody(RefreshRequest(refreshToken))
                            }
                            if (response.status.isSuccess()) {
                                val pair: TokenPair = response.body()
                                tokenProvider.save(pair.accessToken, pair.refreshToken)
                                BearerTokens(pair.accessToken, pair.refreshToken)
                            } else {
                                // The server rejected the refresh token: the session is over.
                                tokenProvider.clear()
                                null
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            // A network failure does not end the session; the next request tries again.
                            AppLog.d("Token refresh failed: ${e::class.simpleName}")
                            null
                        }
                    }
                }
                sendWithoutRequest { request -> trustedHost != null && request.url.host == trustedHost }
            }
        }
    }

    if (options.enableNetworkLogging) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    AppLog.d("HTTP: $message")
                }
            }
            level = LogLevel.INFO
            sanitizeHeader { it.equals(HttpHeaders.Authorization, ignoreCase = true) }
        }
    }

}

/** Returns the problem document, or null when the body is empty or is not a problem document. */
internal fun parseProblem(body: String): ProblemDetails? {
    if (body.isBlank()) return null
    return try {
        apiJson.decodeFromString<ProblemDetails>(body)
    } catch (e: Exception) {
        null
    }
}

/** Builds a client on the given engine; the refresh client shares the engine. Used by tests. */
fun createHttpClient(engine: HttpClientEngine, options: HttpClientOptions = HttpClientOptions()): HttpClient =
    HttpClient(engine) {
        installCommonPlugins(options) { HttpClient(engine) { installBasePlugins(options) } }
    }

expect fun createHttpClient(options: HttpClientOptions = HttpClientOptions()): HttpClient
