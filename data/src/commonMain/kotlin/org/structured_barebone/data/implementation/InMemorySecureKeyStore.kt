package org.structured_barebone.data.implementation

import org.structured_barebone.data.domain.SecureKeyStore

/** Keeps secrets in memory only. For tests; it protects nothing and forgets everything on exit. */
class InMemorySecureKeyStore : SecureKeyStore {
    private val entries = mutableMapOf<String, ByteArray>()

    override suspend fun load(key: String): ByteArray? = entries[key]?.copyOf()

    override suspend fun save(key: String, bytes: ByteArray) {
        entries[key] = bytes.copyOf()
    }

    override suspend fun clear(key: String) {
        entries.remove(key)
    }
}
