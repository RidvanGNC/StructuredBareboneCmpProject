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
import org.structured_barebone.data.domain.AccountRepository
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.CreateCompanyRequest
import org.structured_barebone.data.model.InviteUserRequest
import org.structured_barebone.data.model.UpdatePermissionsRequest
import org.structured_barebone.shared.model.AccountStatusDto
import org.structured_barebone.shared.model.AuditLogEntryDto
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.TenantInvitationDto
import org.structured_barebone.shared.model.TenantInvitationStatus
import org.structured_barebone.shared.model.TenantPermission
import org.structured_barebone.shared.model.TenantUserDto

class AccountRepositoryImpl(
    private val httpClient: HttpClient,
) : BaseRepository(), AccountRepository {

    override suspend fun getStatus(): Result<AccountStatusDto> =
        safeRequest {
            val status = if (ApiConfig.USE_MOCK) {
                MockMailData.accountStatus
            } else {
                httpClient.get("/account/status").body()
            }
            ApiResponse(success = true, message = null, data = status)
        }

    override suspend fun createCompany(name: String): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                MockMailData.accountStatus = MockMailData.accountStatus.copy(
                    hasTenant = true, tenantName = name, isAdmin = true, canBootstrap = false,
                )
            } else {
                httpClient.post("/account/create-company") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateCompanyRequest(name))
                }
            }
            ApiResponse(success = true, message = null, data = Unit)
        }

    override suspend fun getInvitations(): Result<List<TenantInvitationDto>> =
        safeRequest {
            val invitations = if (ApiConfig.USE_MOCK) {
                MockMailData.tenantInvitations.toList()
            } else {
                httpClient.get("/account/invitations").body()
            }
            ApiResponse(success = true, message = null, data = invitations)
        }

    override suspend fun inviteUser(
        email: String,
        permissions: List<TenantPermission>,
    ): Result<TenantInvitationDto> =
        safeRequest {
            val invitation = if (ApiConfig.USE_MOCK) {
                val created = TenantInvitationDto(
                    id = "mock-invite-${MockMailData.tenantInvitations.size + 1}",
                    email = email,
                    permissions = permissions,
                    status = TenantInvitationStatus.Pending,
                    createdAt = "2026-07-17T10:00:00Z",
                    acceptedAt = null,
                )
                MockMailData.tenantInvitations.add(created)
                created
            } else {
                httpClient.post("/account/invitations") {
                    contentType(ContentType.Application.Json)
                    setBody(InviteUserRequest(email, permissions))
                }.body()
            }
            ApiResponse(success = true, message = null, data = invitation)
        }

    override suspend fun revokeInvitation(id: String): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                val index = MockMailData.tenantInvitations.indexOfFirst { it.id == id }
                if (index >= 0) {
                    MockMailData.tenantInvitations[index] =
                        MockMailData.tenantInvitations[index].copy(status = TenantInvitationStatus.Revoked)
                }
            } else {
                httpClient.delete("/account/invitations/$id")
            }
            ApiResponse(success = true, message = null, data = Unit)
        }

    override suspend fun getTenantUsers(): Result<List<TenantUserDto>> =
        safeRequest {
            val users = if (ApiConfig.USE_MOCK) {
                MockMailData.tenantUsers.toList()
            } else {
                httpClient.get("/account/users").body()
            }
            ApiResponse(success = true, message = null, data = users)
        }

    override suspend fun updateMemberPermissions(
        userId: String,
        permissions: List<TenantPermission>,
    ): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                val index = MockMailData.tenantUsers.indexOfFirst { it.id == userId }
                if (index >= 0) {
                    MockMailData.tenantUsers[index] =
                        MockMailData.tenantUsers[index].copy(permissions = permissions)
                }
            } else {
                httpClient.put("/account/users/$userId/permissions") {
                    contentType(ContentType.Application.Json)
                    setBody(UpdatePermissionsRequest(permissions))
                }
            }
            ApiResponse(success = true, message = null, data = Unit)
        }

    override suspend fun getAuditLog(): Result<List<AuditLogEntryDto>> =
        safeRequest {
            val entries = if (ApiConfig.USE_MOCK) {
                MockMailData.auditLog.toList()
            } else {
                httpClient.get("/account/audit-log").body()
            }
            ApiResponse(success = true, message = null, data = entries)
        }
}
