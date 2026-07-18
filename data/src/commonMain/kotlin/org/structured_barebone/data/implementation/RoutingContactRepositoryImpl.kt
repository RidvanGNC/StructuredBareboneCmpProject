package org.structured_barebone.data.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.structured_barebone.data.client.ApiConfig
import org.structured_barebone.data.domain.RoutingContactRepository
import org.structured_barebone.data.model.AddRoutingContactRequest
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.SetRoutingContactAvailabilityRequest
import org.structured_barebone.data.model.UpdateRoutingContactRequest
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.RoutingContactDto

class RoutingContactRepositoryImpl(
    private val httpClient: HttpClient,
) : BaseRepository(), RoutingContactRepository {

    override suspend fun getActive(): Result<List<RoutingContactDto>> =
        safeRequest {
            val contacts = if (ApiConfig.USE_MOCK) {
                MockMailData.routingContacts.filter { it.isActive }
                    .sortedWith(compareBy({ it.priority }, { it.name }))
            } else {
                httpClient.get("/routing-contacts").body()
            }
            ApiResponse(success = true, message = null, data = contacts)
        }

    override suspend fun add(
        name: String,
        surname: String,
        email: String,
        languagePreferences: List<String>,
        isAvailable: Boolean,
        priority: Int,
    ): Result<RoutingContactDto> =
        safeRequest {
            val contact = if (ApiConfig.USE_MOCK) {
                RoutingContactDto(
                    id = "mock-contact-${MockMailData.routingContacts.size + 1}",
                    name = name,
                    surname = surname,
                    email = email,
                    languagePreferences = languagePreferences,
                    addedAt = "2026-07-17T10:00:00Z",
                    removedAt = null,
                    isActive = true,
                    isAvailable = isAvailable,
                    priority = priority,
                ).also { MockMailData.routingContacts.add(it) }
            } else {
                httpClient.post("/routing-contacts") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        AddRoutingContactRequest(
                            name,
                            surname,
                            email,
                            languagePreferences,
                            isAvailable,
                            priority,
                        ),
                    )
                }.body()
            }
            ApiResponse(success = true, message = null, data = contact)
        }

    override suspend fun update(
        id: String,
        name: String,
        surname: String,
        email: String,
        languagePreferences: List<String>,
        isAvailable: Boolean,
        priority: Int,
    ): Result<RoutingContactDto> =
        safeRequest {
            val contact = if (ApiConfig.USE_MOCK) {
                val index = MockMailData.routingContacts.indexOfFirst { it.id == id }
                require(index >= 0) { "Kayit bulunamadi" }
                val updated = MockMailData.routingContacts[index].copy(
                    name = name,
                    surname = surname,
                    email = email,
                    languagePreferences = languagePreferences,
                    isAvailable = isAvailable,
                    priority = priority,
                )
                MockMailData.routingContacts[index] = updated
                updated
            } else {
                httpClient.put("/routing-contacts/$id") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        UpdateRoutingContactRequest(
                            name,
                            surname,
                            email,
                            languagePreferences,
                            isAvailable,
                            priority,
                        ),
                    )
                }.body()
            }
            ApiResponse(success = true, message = null, data = contact)
        }

    override suspend fun setAvailability(id: String, isAvailable: Boolean): Result<RoutingContactDto> =
        safeRequest {
            val contact = if (ApiConfig.USE_MOCK) {
                val index = MockMailData.routingContacts.indexOfFirst { it.id == id }
                require(index >= 0) { "Kayit bulunamadi" }
                val updated = MockMailData.routingContacts[index].copy(isAvailable = isAvailable)
                MockMailData.routingContacts[index] = updated
                updated
            } else {
                httpClient.patch("/routing-contacts/$id/availability") {
                    contentType(ContentType.Application.Json)
                    setBody(SetRoutingContactAvailabilityRequest(isAvailable))
                }.body()
            }
            ApiResponse(success = true, message = null, data = contact)
        }

    override suspend fun remove(id: String): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                val index = MockMailData.routingContacts.indexOfFirst { it.id == id }
                require(index >= 0) { "Kayit bulunamadi" }
                val updated = MockMailData.routingContacts[index].copy(
                    isActive = false,
                    removedAt = "2026-07-17T10:00:00Z",
                )
                MockMailData.routingContacts[index] = updated
            } else {
                httpClient.delete("/routing-contacts/$id")
            }
            ApiResponse(success = true, message = null, data = Unit)
        }
}
