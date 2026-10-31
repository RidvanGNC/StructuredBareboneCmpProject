package org.structured_barebone.data.client

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/** One step of the sign-out checklist, for example telling the server to forget this device. */
fun interface SignOutStep {
    suspend fun run()
}

/**
 * Signs the user out. Steps run first, in order, while the tokens still exist (server calls need them); a
 * step that fails or exceeds [stepTimeoutMillis] is skipped so an offline device is never stuck. The tokens
 * are cleared last and always, even when a step or the caller is cancelled.
 */
class SessionCleaner(
    private val tokens: TokenProvider,
    private val steps: List<SignOutStep> = emptyList(),
    private val stepTimeoutMillis: Long = DEFAULT_STEP_TIMEOUT_MILLIS,
) {
    suspend fun signOut() {
        try {
            for (step in steps) {
                withTimeoutOrNull(stepTimeoutMillis) {
                    try {
                        step.run()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        AppLog.d("Sign-out step failed: ${e::class.simpleName}")
                    }
                }
            }
        } finally {
            withContext(NonCancellable) { tokens.clear() }
        }
    }

    companion object {
        const val DEFAULT_STEP_TIMEOUT_MILLIS = 5_000L
    }
}
