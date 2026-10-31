package org.structured_barebone.data.client

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.structured_barebone.data.domain.SecureKeyStore

/**
 * Persists the session tokens. The tokens live in the [SecureKeyStore]; `Settings` only holds the
 * non-secret "a session exists" flag so [isLoggedIn] has a correct value before the first token read.
 *
 * When the flag says "signed in" but the secure store cannot return the tokens (for example a device backup
 * restored on another device), the session is treated as ended and the user signs in again.
 */
class TokenStorage(
    private val settings: Settings,
    private val secureKeyStore: SecureKeyStore,
) : TokenProvider {
    @Serializable
    private class Session(val access: String, val refresh: String)

    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private var cached: Session? = null
    private var loaded = false

    private val mutableLoggedIn = MutableStateFlow(settings.getBoolean(HAS_SESSION_KEY, false))

    override val isLoggedIn: StateFlow<Boolean> = mutableLoggedIn.asStateFlow()

    override suspend fun accessToken(): String? = session()?.access

    override suspend fun refreshToken(): String? = session()?.refresh

    override suspend fun save(accessToken: String, refreshToken: String) {
        mutex.withLock {
            val session = Session(accessToken, refreshToken)
            secureKeyStore.save(SECURE_KEY, json.encodeToString(Session.serializer(), session).encodeToByteArray())
            settings.putBoolean(HAS_SESSION_KEY, true)
            cached = session
            loaded = true
            mutableLoggedIn.value = true
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            secureKeyStore.clear(SECURE_KEY)
            settings.remove(HAS_SESSION_KEY)
            cached = null
            loaded = true
            mutableLoggedIn.value = false
        }
    }

    private suspend fun session(): Session? = mutex.withLock {
        if (loaded) return@withLock cached
        val session = secureKeyStore.load(SECURE_KEY)?.let {
            try {
                json.decodeFromString(Session.serializer(), it.decodeToString())
            } catch (_: Exception) {
                null
            }
        }
        cached = session
        loaded = true
        if (session == null && mutableLoggedIn.value) {
            settings.remove(HAS_SESSION_KEY)
            mutableLoggedIn.value = false
        }
        session
    }

    private companion object {
        const val SECURE_KEY = "session"
        const val HAS_SESSION_KEY = "has_session"
    }
}
