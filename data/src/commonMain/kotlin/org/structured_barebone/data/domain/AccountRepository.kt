package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.AccountStatusDto
import org.structured_barebone.shared.model.AuditLogEntryDto
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.TenantInvitationDto
import org.structured_barebone.shared.model.TenantPermission
import org.structured_barebone.shared.model.TenantUserDto

interface AccountRepository {
    suspend fun getStatus(): Result<AccountStatusDto>

    suspend fun createCompany(name: String): Result<Unit>

    suspend fun getInvitations(): Result<List<TenantInvitationDto>>

    suspend fun inviteUser(email: String, permissions: List<TenantPermission>): Result<TenantInvitationDto>

    suspend fun revokeInvitation(id: String): Result<Unit>

    suspend fun getTenantUsers(): Result<List<TenantUserDto>>

    suspend fun updateMemberPermissions(userId: String, permissions: List<TenantPermission>): Result<Unit>

    suspend fun getAuditLog(): Result<List<AuditLogEntryDto>>
}
