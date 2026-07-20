package org.structured_barebone.data.platform

// Ek belgeyi cihaza kaydedip varsayilan bir goruntuleyici/paylasim sayfasiyla acan platforma ozgu servis.
interface AttachmentSaver {
    fun saveAndOpen(fileName: String, bytes: ByteArray)
}
