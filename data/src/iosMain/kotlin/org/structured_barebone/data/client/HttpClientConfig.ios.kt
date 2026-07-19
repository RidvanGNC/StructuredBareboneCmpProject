package org.structured_barebone.data.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClient(
    tokenProvider: suspend () -> String?,
    graphTokenProvider: suspend () -> String?,
): HttpClient {
    return HttpClient(Darwin) {
        installCommonPlugins(tokenProvider, graphTokenProvider)
    }
}