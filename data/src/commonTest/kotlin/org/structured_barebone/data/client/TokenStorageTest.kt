package org.structured_barebone.data.client

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import org.structured_barebone.data.implementation.InMemorySecureKeyStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenStorageTest {
    private fun dump(settings: MapSettings) =
        settings.keys.joinToString("|") { "$it=${settings.getStringOrNull(it) ?: settings.getBooleanOrNull(it)}" }

    @Test
    fun tokens_are_saved_read_and_cleared() = runTest {
        val storage = TokenStorage(MapSettings(), InMemorySecureKeyStore())
        assertFalse(storage.isLoggedIn.value)

        storage.save("access-1", "refresh-1")

        assertEquals("access-1", storage.accessToken())
        assertEquals("refresh-1", storage.refreshToken())
        assertTrue(storage.isLoggedIn.value)

        storage.clear()

        assertNull(storage.accessToken())
        assertNull(storage.refreshToken())
        assertFalse(storage.isLoggedIn.value)
    }

    @Test
    fun plain_settings_never_contain_the_tokens() = runTest {
        val settings = MapSettings()
        TokenStorage(settings, InMemorySecureKeyStore()).save("secret-access", "secret-refresh")

        val content = dump(settings)

        assertFalse(content.contains("secret-access"), content)
        assertFalse(content.contains("secret-refresh"), content)
        assertTrue(content.contains("has_session"), content)
    }

    @Test
    fun a_new_instance_restores_the_session_from_the_secure_store() = runTest {
        val settings = MapSettings()
        val keys = InMemorySecureKeyStore()
        TokenStorage(settings, keys).save("a", "r")

        val reopened = TokenStorage(settings, keys)

        assertTrue(reopened.isLoggedIn.value)
        assertEquals("a", reopened.accessToken())
        assertEquals("r", reopened.refreshToken())
    }

    @Test
    fun an_unreadable_secure_store_ends_the_session_instead_of_crashing() = runTest {
        val settings = MapSettings("has_session" to true)
        val storage = TokenStorage(settings, InMemorySecureKeyStore())
        assertTrue(storage.isLoggedIn.value)

        assertNull(storage.accessToken())

        assertFalse(storage.isLoggedIn.value)
        assertFalse(settings.hasKey("has_session"))
    }

    @Test
    fun a_corrupt_secure_entry_ends_the_session() = runTest {
        val settings = MapSettings("has_session" to true)
        val keys = InMemorySecureKeyStore().also { it.save("session", "not json".encodeToByteArray()) }
        val storage = TokenStorage(settings, keys)

        assertNull(storage.refreshToken())

        assertFalse(storage.isLoggedIn.value)
    }

    @Test
    fun saving_again_replaces_the_old_tokens() = runTest {
        val storage = TokenStorage(MapSettings(), InMemorySecureKeyStore())
        storage.save("a1", "r1")
        storage.save("a2", "r2")

        assertEquals("a2", storage.accessToken())
        assertEquals("r2", storage.refreshToken())
    }

    @Test
    fun a_failed_write_does_not_report_a_session() = runTest {
        val failing = object : org.structured_barebone.data.domain.SecureKeyStore {
            override suspend fun load(key: String): ByteArray? = null
            override suspend fun save(key: String, bytes: ByteArray) = error("write failed")
            override suspend fun clear(key: String) = Unit
        }
        val storage = TokenStorage(MapSettings(), failing)

        val failure = runCatching { storage.save("a", "r") }

        assertTrue(failure.isFailure)
        assertFalse(storage.isLoggedIn.value)
        assertNull(storage.accessToken())
    }
}
