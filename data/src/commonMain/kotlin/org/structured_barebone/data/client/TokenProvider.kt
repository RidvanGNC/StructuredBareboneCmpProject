package org.structured_barebone.data.client

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Source of the session tokens used by the HTTP client. Persistence and encryption belong to the implementation. */
interface TokenProvider {
    /** Emits false when the session ended outside the UI (for example the refresh token was rejected). */
    val isLoggedIn: StateFlow<Boolean>

    suspend fun accessToken(): String?
    suspend fun refreshToken(): String?
    suspend fun save(accessToken: String, refreshToken: String)
    suspend fun clear()
}

/** Keeps tokens in memory only. For tests and short-lived tools; never use it for a real session. */
class InMemoryTokenProvider(accessToken: String? = null, refreshToken: String? = null) : TokenProvider {
    private var access: String? = accessToken
    private var refresh: String? = refreshToken
    private val mutableLoggedIn = MutableStateFlow(refreshToken != null)

    override val isLoggedIn: StateFlow<Boolean> = mutableLoggedIn.asStateFlow()

    override suspend fun accessToken(): String? = access
    override suspend fun refreshToken(): String? = refresh

    override suspend fun save(accessToken: String, refreshToken: String) {
        access = accessToken
        refresh = refreshToken
        mutableLoggedIn.value = true
    }

    override suspend fun clear() {
        access = null
        refresh = null
        mutableLoggedIn.value = false
    }
}
