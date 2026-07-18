package org.structured_barebone.data.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

actual fun createHttpClient(tokenProvider: suspend () -> String?): HttpClient {
    return HttpClient(Android) {
        engine {
            sslManager = { httpsURLConnection ->
                httpsURLConnection.sslSocketFactory = getSSLSocketFactory()
            }
        }
        installCommonPlugins(tokenProvider)
    }
}

private fun getSSLSocketFactory(): SSLSocketFactory {
    val trustManagerFactory = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm()
    )
    trustManagerFactory.init(null as KeyStore?)
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustManagerFactory.trustManagers, null)
    return sslContext.socketFactory
}