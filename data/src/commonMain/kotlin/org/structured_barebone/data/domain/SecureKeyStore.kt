package org.structured_barebone.data.domain

/**
 * Stores small secrets (tokens, keys) under the protection of the operating system. Values never reach
 * `Settings`, which is plain text on every platform.
 *
 * An entry that cannot be read (restored backup, removed key, corrupt file) is reported as `null`, never as
 * an exception: callers treat it as "no secret" and ask the user to sign in again.
 */
interface SecureKeyStore {
    suspend fun load(key: String): ByteArray?
    suspend fun save(key: String, bytes: ByteArray)
    suspend fun clear(key: String)
}
