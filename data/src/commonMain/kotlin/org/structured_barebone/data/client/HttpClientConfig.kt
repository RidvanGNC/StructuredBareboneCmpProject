package org.structured_barebone.data.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.structured_barebone.data.model.ProblemDetails
import org.structured_barebone.data.model.ServerException

fun HttpClientConfig<*>.installCommonPlugins() {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    install(ContentNegotiation) {
        json(json)
    }

    install(HttpTimeout) {
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 30_000
        requestTimeoutMillis = 60_000
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("KTOR: $message")
            }
        }
        level = LogLevel.BODY
    }

    HttpResponseValidator {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                val rawBody = response.bodyAsText()
                val errorMessage = try {
                    val problemDetails = json.decodeFromString<ProblemDetails>(rawBody)
                    problemDetails.toErrorMessage()
                } catch (e: Exception) {
                    rawBody
                }
                throw ServerException(
                    code = response.status.value, description = errorMessage
                )
            }
        }
    }
}

expect fun createHttpClient(): HttpClient