package org.structured_barebone.data.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual fun createHttpClient(): HttpClient {
    return HttpClient(Android) {
        installCommonPlugins()
    }
}
