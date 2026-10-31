package org.structured_barebone.data.implementation

import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JvmSecureKeyStoreTest {
    private val isWindows = System.getProperty("os.name").startsWith("Windows")
    private val root: File = Files.createTempDirectory("secure-store-test").toFile()
    private val store = JvmSecureKeyStore("TestApp", root)
    private val entryFile = File(root, "TestApp/session.bin")

    @AfterTest
    fun cleanUp() {
        root.deleteRecursively()
    }

    @Test
    fun save_load_and_clear_round_trip() = runBlocking {
        val secret = ByteArray(161) { (it * 7 + 3).toByte() }

        assertNull(store.load("session"))
        store.save("session", secret)
        assertContentEquals(secret, store.load("session"))

        store.clear("session")
        assertNull(store.load("session"))
    }

    @Test
    fun the_stored_file_does_not_contain_the_secret_in_plain_text_on_windows() = runBlocking {
        if (!isWindows) return@runBlocking
        val secret = "plain-text-marker-0123456789".encodeToByteArray()

        store.save("session", secret)

        val onDisk = entryFile.readBytes()
        assertTrue(onDisk.size > secret.size)
        assertEquals(-1, String(onDisk, Charsets.ISO_8859_1).indexOf("plain-text-marker"))
    }

    @Test
    fun a_corrupt_file_reads_as_no_secret_on_windows() = runBlocking {
        if (!isWindows) return@runBlocking
        store.save("session", byteArrayOf(1, 2, 3))
        entryFile.writeBytes(byteArrayOf(9, 9, 9, 9))

        assertNull(store.load("session"))
    }

    @Test
    fun entries_are_independent() = runBlocking {
        store.save("first", byteArrayOf(1))
        store.save("second", byteArrayOf(2))
        store.clear("first")

        assertNull(store.load("first"))
        assertContentEquals(byteArrayOf(2), store.load("second"))
    }

    @Test
    fun path_characters_in_a_key_cannot_leave_the_store_directory() = runBlocking {
        store.save("../../escape", byteArrayOf(1))

        assertFalse(File(root, "escape.bin").exists())
        assertTrue(File(root, "TestApp/escape.bin").exists())
    }

    @Test
    fun a_key_without_usable_characters_is_rejected() = runBlocking {
        assertFailsWith<IllegalArgumentException> { store.save("../..", byteArrayOf(1)) }
        Unit
    }
}
