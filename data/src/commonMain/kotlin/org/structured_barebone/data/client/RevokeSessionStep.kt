package org.structured_barebone.data.client

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.structured_barebone.data.model.RefreshRequest

/** Sign-out step that tells the server to revoke this device's refresh token (`POST /v1/auth/logout`). */
class RevokeSessionStep(
    private val httpClient: HttpClient,
    private val config: AppConfig,
    private val tokens: TokenProvider,
) : SignOutStep {
    override suspend fun run() {
        if (!config.authEnabled) return
        val refreshToken = tokens.refreshToken() ?: return
        httpClient.post(config.url(AuthPaths.LOGOUT)) {
            contentType(ContentType.Application.Json)
            setBody(RefreshRequest(refreshToken))
        }
    }
}
