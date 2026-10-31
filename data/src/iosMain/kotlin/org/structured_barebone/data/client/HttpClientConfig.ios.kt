package org.structured_barebone.data.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClient(options: HttpClientOptions): HttpClient = HttpClient(Darwin) {
    installCommonPlugins(options) { HttpClient(Darwin) { installBasePlugins(options) } }
}
