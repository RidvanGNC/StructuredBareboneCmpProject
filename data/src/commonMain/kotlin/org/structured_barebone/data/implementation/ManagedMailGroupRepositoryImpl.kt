package org.structured_barebone.data.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.structured_barebone.data.client.ApiConfig
import org.structured_barebone.data.domain.ManagedMailGroupRepository
import org.structured_barebone.data.model.AddManagedMailGroupRequest
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.SetMailGroupActiveRequest
import org.structured_barebone.data.model.SetMailGroupNoteRequest
import org.structured_barebone.shared.model.ManagedMailGroupDto
import org.structured_barebone.shared.model.Result

class ManagedMailGroupRepositoryImpl(
    private val httpClient: HttpClient,
) : BaseRepository(), ManagedMailGroupRepository {

    override suspend fun getAll(): Result<List<ManagedMailGroupDto>> =
        safeRequest {
            val groups = if (ApiConfig.USE_MOCK) {
                MockMailData.managedMailGroups.toList()
            } else {
                httpClient.get("/managed-mail-groups").body()
            }
            ApiResponse(success = true, message = null, data = groups)
        }

    override suspend fun add(mailGroup: String): Result<ManagedMailGroupDto> =
        safeRequest {
            val created = if (ApiConfig.USE_MOCK) {
                val new = ManagedMailGroupDto(
                    id = "mock-mg-${MockMailData.managedMailGroups.size + 1}",
                    mailGroup = mailGroup,
                    isActive = true,
                    note = null,
                    addedAt = "2026-07-20T00:00:00Z",
                )
                MockMailData.managedMailGroups.add(new)
                new
            } else {
                httpClient.post("/managed-mail-groups") {
                    contentType(ContentType.Application.Json)
                    setBody(AddManagedMailGroupRequest(mailGroup))
                }.body()
            }
            ApiResponse(success = true, message = null, data = created)
        }

    override suspend fun setActive(id: String, isActive: Boolean): Result<ManagedMailGroupDto> =
        safeRequest {
            val updated = if (ApiConfig.USE_MOCK) {
                val index = MockMailData.managedMailGroups.indexOfFirst { it.id == id }
                require(index >= 0) { "Kayit bulunamadi" }
                val result = MockMailData.managedMailGroups[index].copy(isActive = isActive)
                MockMailData.managedMailGroups[index] = result
                result
            } else {
                httpClient.put("/managed-mail-groups/$id/active") {
                    contentType(ContentType.Application.Json)
                    setBody(SetMailGroupActiveRequest(isActive))
                }.body()
            }
            ApiResponse(success = true, message = null, data = updated)
        }

    override suspend fun setNote(id: String, note: String?): Result<ManagedMailGroupDto> =
        safeRequest {
            val updated = if (ApiConfig.USE_MOCK) {
                val index = MockMailData.managedMailGroups.indexOfFirst { it.id == id }
                require(index >= 0) { "Kayit bulunamadi" }
                val result = MockMailData.managedMailGroups[index].copy(note = note)
                MockMailData.managedMailGroups[index] = result
                result
            } else {
                httpClient.put("/managed-mail-groups/$id/note") {
                    contentType(ContentType.Application.Json)
                    setBody(SetMailGroupNoteRequest(note))
                }.body()
            }
            ApiResponse(success = true, message = null, data = updated)
        }

    override suspend fun delete(id: String): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                val index = MockMailData.managedMailGroups.indexOfFirst { it.id == id }
                require(index >= 0) { "Kayit bulunamadi" }
                MockMailData.managedMailGroups.removeAt(index)
            } else {
                httpClient.delete("/managed-mail-groups/$id")
            }
            ApiResponse(success = true, message = null, data = Unit)
        }
}
