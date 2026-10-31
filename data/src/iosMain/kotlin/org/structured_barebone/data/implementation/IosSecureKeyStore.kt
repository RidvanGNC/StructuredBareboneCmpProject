package org.structured_barebone.data.implementation

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import org.structured_barebone.data.domain.SecureKeyStore
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.posix.memcpy

/**
 * Keychain-backed store: a generic password item per key, readable only on this device while it is unlocked
 * and never moved to a backup or another device.
 *
 * Unverified: iOS targets cannot be built or run on the development machine used for this template.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosSecureKeyStore(private val service: String = DEFAULT_SERVICE) : SecureKeyStore {
    override suspend fun load(key: String): ByteArray? = memScoped {
        val query = baseQuery(key)
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        val result = alloc<CFTypeRefVar>()
        if (SecItemCopyMatching(query, result.ptr) != errSecSuccess) return@memScoped null
        val data = CFBridgingRelease(result.value) as? NSData ?: return@memScoped null
        ByteArray(data.length.toInt()).also { bytes ->
            if (bytes.isNotEmpty()) bytes.usePinned { memcpy(it.addressOf(0), data.bytes, data.length) }
        }
    }

    override suspend fun save(key: String, bytes: ByteArray) {
        clear(key)
        val query = baseQuery(key)
        val data = bytes.usePinned { NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong()) }
        CFDictionaryAddValue(query, kSecValueData, CFBridgingRetain(data))
        CFDictionaryAddValue(query, kSecAttrAccessible, kSecAttrAccessibleWhenUnlockedThisDeviceOnly)
        check(SecItemAdd(query, null) == errSecSuccess) { "The secure entry could not be written." }
    }

    override suspend fun clear(key: String) {
        SecItemDelete(baseQuery(key))
    }

    private fun baseQuery(key: String) = CFDictionaryCreateMutable(null, 0, null, null).also {
        CFDictionaryAddValue(it, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(it, kSecAttrService, CFBridgingRetain(service))
        CFDictionaryAddValue(it, kSecAttrAccount, CFBridgingRetain(key))
    }

    companion object {
        /** Rename together with the bundle id when the template becomes a product. */
        const val DEFAULT_SERVICE = "org.structured_barebone.secure_store"
    }
}
