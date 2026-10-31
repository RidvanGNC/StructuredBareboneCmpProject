package org.structured_barebone.data.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual fun createHttpClient(options: HttpClientOptions): HttpClient = HttpClient(Android) {
    installCommonPlugins(options) { HttpClient(Android) { installBasePlugins(options) } }
}
