package org.structured_barebone.data.platform

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

class AndroidAttachmentSaver(private val context: Context) : AttachmentSaver {
    override fun saveAndOpen(fileName: String, bytes: ByteArray) {
        val dir = File(context.cacheDir, "attachments").apply { mkdirs() }
        val file = File(dir, fileName)
        file.writeBytes(bytes)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, context.contentResolver.getType(uri) ?: "*/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
