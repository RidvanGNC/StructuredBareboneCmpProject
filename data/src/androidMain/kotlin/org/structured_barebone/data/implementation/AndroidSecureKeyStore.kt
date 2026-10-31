package org.structured_barebone.data.implementation

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.structured_barebone.data.domain.SecureKeyStore
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Secrets are encrypted with AES-256-GCM and kept in private SharedPreferences. The AES key is generated
 * inside the Android Keystore and cannot be exported. After a backup restore or a device change the key is
 * missing, decryption fails and the entry reads as `null`.
 */
class AndroidSecureKeyStore(context: Context) : SecureKeyStore {
    private val preferences = context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override suspend fun load(key: String): ByteArray? = withContext(Dispatchers.IO) {
        val stored = preferences.getString(key, null) ?: return@withContext null
        try {
            val raw = Base64.decode(stored, Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, wrappingKey(), GCMParameterSpec(TAG_BITS, raw, 0, IV_BYTES))
            cipher.doFinal(raw, IV_BYTES, raw.size - IV_BYTES)
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun save(key: String, bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, wrappingKey())
            val encrypted = cipher.doFinal(bytes)
            val stored = Base64.encodeToString(cipher.iv + encrypted, Base64.NO_WRAP)
            check(preferences.edit().putString(key, stored).commit()) { "The secure entry could not be written." }
        }
    }

    override suspend fun clear(key: String) {
        withContext(Dispatchers.IO) {
            preferences.edit().remove(key).commit()
        }
    }

    private fun wrappingKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_BITS)
                .build(),
        )
        return generator.generateKey()
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "secure_store_wrapping_key"
        const val PREFERENCES_NAME = "secure_store"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_BITS = 256
        const val IV_BYTES = 12
        const val TAG_BITS = 128
    }
}
