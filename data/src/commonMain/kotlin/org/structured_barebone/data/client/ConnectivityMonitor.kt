package org.structured_barebone.data.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.retry
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import org.structured_barebone.data.model.ServerException

/**
 * Tracks whether the server is reachable by probing [healthUrl] (`GET /health`, see `contracts/common.openapi.yaml`).
 * Any answer other than a gateway error (502..504) counts as reachable. Starts as offline until the first probe.
 */
class ConnectivityMonitor(
    private val httpClient: HttpClient,
    private val healthUrl: String,
) {
    private val mutableOnline = MutableStateFlow(false)
    val online: StateFlow<Boolean> = mutableOnline.asStateFlow()

    /** @param quick short timeouts and no retry, for a probe the user is waiting for. */
    suspend fun check(quick: Boolean = false): Boolean {
        val reachable = try {
            httpClient.get(healthUrl) {
                if (quick) {
                    timeout {
                        connectTimeoutMillis = HttpDefaults.QUICK_PROBE_TIMEOUT_MS
                        requestTimeoutMillis = HttpDefaults.QUICK_PROBE_TIMEOUT_MS
                        socketTimeoutMillis = HttpDefaults.QUICK_PROBE_TIMEOUT_MS
                    }
                    retry { maxRetries = 0 }
                }
            }
            true
        } catch (e: ServerException) {
            e.status !in GATEWAY_ERRORS
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
        mutableOnline.value = reachable
        return reachable
    }

    /** Probes until cancelled: every [baseIntervalMillis] while online, doubling up to [maxIntervalMillis] while offline. */
    suspend fun run(
        baseIntervalMillis: Long = HttpDefaults.PROBE_INTERVAL_MS,
        maxIntervalMillis: Long = HttpDefaults.PROBE_MAX_INTERVAL_MS,
    ) {
        var wait = baseIntervalMillis
        while (currentCoroutineContext().isActive) {
            wait = if (check()) baseIntervalMillis else (wait * 2).coerceAtMost(maxIntervalMillis)
            delay(wait)
        }
    }

    private companion object {
        val GATEWAY_ERRORS = 502..504
    }
}
