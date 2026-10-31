package org.structured_barebone.data.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.structured_barebone.data.client.AppConfig
import org.structured_barebone.data.client.AuthPaths
import org.structured_barebone.data.client.TokenProvider
import org.structured_barebone.data.domain.AuthRepository
import org.structured_barebone.data.model.LoginRequest
import org.structured_barebone.data.model.TokenPair
import org.structured_barebone.data.model.UserProfile
import org.structured_barebone.shared.model.Result

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val config: AppConfig,
    private val tokens: TokenProvider,
) : BaseRepository(), AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> = safeCall {
        val pair: TokenPair = httpClient.post(config.url(AuthPaths.LOGIN)) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email.trim(), password))
        }.body()
        tokens.save(pair.accessToken, pair.refreshToken)
    }

    override suspend fun profile(): Result<UserProfile> = safeCall {
        httpClient.get(config.url(AuthPaths.ME)).body<UserProfile>()
    }
}
