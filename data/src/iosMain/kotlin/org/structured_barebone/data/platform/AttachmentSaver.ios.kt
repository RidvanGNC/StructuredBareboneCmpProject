package org.structured_barebone.data.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.UIKit.UIApplication

// NOT: gercek bir "paylas" sayfasi (UIActivityViewController) sunmak icin mevcut UIViewController
// referansina ihtiyac var; bu servis arayuzu (AttachmentSaver) platform-bagimsiz tutuldugu icin
// bunu tasimiyor. Basitlestirilmis surum: dosyayi gecici dizine yazip iOS'un kayitli varsayilan
// uygulamayla acmasini dener (her dosya turu icin calismayabilir). Mac/Xcode olmadan derlenip
// dogrulanamadi.
class IosAttachmentSaver : AttachmentSaver {
    @OptIn(ExperimentalForeignApi::class)
    override fun saveAndOpen(fileName: String, bytes: ByteArray) {
        val nsData = bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }
        val path = NSTemporaryDirectory() + fileName
        val url = NSURL.fileURLWithPath(path)
        nsData.writeToFile(path, atomically = true)
        UIApplication.sharedApplication.openURL(url)
    }
}
