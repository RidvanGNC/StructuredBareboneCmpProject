package org.structured_barebone.data.implementation

import com.sun.jna.platform.win32.Crypt32Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.structured_barebone.data.domain.SecureKeyStore
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions

/**
 * Desktop store. On Windows each entry is a file protected with DPAPI (user scope), so it cannot be read
 * outside the same user account. On other operating systems the file is only restricted to its owner (0600),
 * which is weaker and meant for development and secondary platforms.
 *
 * Entries live in `%APPDATA%\[appDirectoryName]` on Windows and `~/.[appDirectoryName]` elsewhere. Rename
 * [appDirectoryName] together with the application id when the template becomes a product.
 */
class JvmSecureKeyStore(
    private val appDirectoryName: String = DEFAULT_DIRECTORY_NAME,
    private val baseDirectory: File? = null,
) : SecureKeyStore {
    private val isWindows = System.getProperty("os.name").orEmpty().startsWith("Windows", ignoreCase = true)

    override suspend fun load(key: String): ByteArray? = withContext(Dispatchers.IO) {
        val file = fileFor(key)
        if (!file.exists()) return@withContext null
        try {
            val raw = file.readBytes()
            if (isWindows) Crypt32Util.cryptUnprotectData(raw) else raw
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun save(key: String, bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            val file = fileFor(key)
            file.parentFile.mkdirs()
            file.writeBytes(if (isWindows) Crypt32Util.cryptProtectData(bytes) else bytes)
            if (!isWindows) {
                runCatching { Files.setPosixFilePermissions(file.toPath(), PosixFilePermissions.fromString("rw-------")) }
            }
        }
    }

    override suspend fun clear(key: String) {
        withContext(Dispatchers.IO) {
            fileFor(key).delete()
        }
    }

    private fun fileFor(key: String): File {
        val safeName = key.filter { it.isLetterOrDigit() || it == '-' || it == '_' }
        require(safeName.isNotEmpty()) { "The key has no usable characters." }
        return File(directory(), "$safeName.bin")
    }

    private fun directory(): File {
        baseDirectory?.let { return File(it, appDirectoryName) }
        val home = System.getProperty("user.home")
        return if (isWindows) File(System.getenv("APPDATA") ?: home, appDirectoryName) else File(home, ".$appDirectoryName")
    }

    companion object {
        const val DEFAULT_DIRECTORY_NAME = "StructuredBarebone"
    }
}
