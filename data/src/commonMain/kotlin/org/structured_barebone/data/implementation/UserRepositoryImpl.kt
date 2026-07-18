package org.structured_barebone.data.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import org.structured_barebone.data.client.ApiConfig
import org.structured_barebone.data.domain.UserRepository
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.UserProfile

class UserRepositoryImpl(
    private val httpClient: HttpClient,
) : BaseRepository(), UserRepository {

    override suspend fun getCurrentUser(): Result<UserProfile> =
        safeRequest {
            val profile = if (ApiConfig.USE_MOCK) {
                UserProfile(
                    id = "u1",
                    email = "dragongonca@gmail.com",
                    name = "Ridvan",
                    surname = "Gonca",
                    phoneNumber = null,
                    firstLoginAt = "2026-01-31T01:12:00Z",
                    lastLoginAt = "2026-07-16T15:12:00Z",
                    isActive = true,
                    deletionScheduledAt = null,
                    language = "tr",
                )
            } else {
                httpClient.get("/me").body()
            }
            ApiResponse(success = true, message = null, data = profile)
        }

    override suspend fun requestAccountDeletion(): Result<Unit> =
        safeRequest {
            if (!ApiConfig.USE_MOCK) {
                httpClient.delete("/me")
            }
            ApiResponse(success = true, message = null, data = Unit)
        }
}
