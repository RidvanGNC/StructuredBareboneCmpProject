package org.structured_barebone.data.client

/** Fixed values of the HTTP client. */
object HttpDefaults {
    const val VERSION_HEADER = "X-Client-Version"
    const val CONNECT_TIMEOUT_MS = 15_000L
    const val SOCKET_TIMEOUT_MS = 30_000L
    const val REQUEST_TIMEOUT_MS = 60_000L
    const val REFRESH_TIMEOUT_MS = 10_000L
    const val QUICK_PROBE_TIMEOUT_MS = 3_000L
    const val MAX_RETRIES = 2
    const val RETRY_BASE_DELAY_MS = 500L
    const val RETRY_MAX_DELAY_MS = 4_000L
    const val RETRY_JITTER_MS = 1_000L
    const val PROBE_INTERVAL_MS = 30_000L
    const val PROBE_MAX_INTERVAL_MS = 120_000L
}

/**
 * Configuration of the shared HTTP client.
 *
 * @property tokenProvider enables bearer authentication when set.
 * @property refreshUrl absolute URL that exchanges a refresh token for a new token pair. Tokens are
 * sent without a prior challenge only to the host of this URL; other hosts never receive them unprompted.
 * @property updateGate receives the "client version no longer supported" signal (HTTP 426).
 * @property enableNetworkLogging logs request lines through [AppLog]; the Authorization header is masked.
 */
data class HttpClientOptions(
    val tokenProvider: TokenProvider? = null,
    val refreshUrl: String? = null,
    val updateGate: UpdateGate? = null,
    val enableNetworkLogging: Boolean = false,
    val connectTimeoutMillis: Long = HttpDefaults.CONNECT_TIMEOUT_MS,
    val socketTimeoutMillis: Long = HttpDefaults.SOCKET_TIMEOUT_MS,
    val requestTimeoutMillis: Long = HttpDefaults.REQUEST_TIMEOUT_MS,
    val maxRetries: Int = HttpDefaults.MAX_RETRIES,
    val retryBaseDelayMillis: Long = HttpDefaults.RETRY_BASE_DELAY_MS,
    val retryJitterMillis: Long = HttpDefaults.RETRY_JITTER_MS,
)
