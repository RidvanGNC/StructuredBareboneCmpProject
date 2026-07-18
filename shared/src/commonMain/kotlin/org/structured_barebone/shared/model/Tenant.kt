package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class TenantInvitationStatus {
    Pending,
    Accepted,
    Revoked,
}

@Serializable
enum class TenantPermission {
    ViewMails,
    ApproveMails,
    SendMails,
    AddRoutingContact,
    RemoveRoutingContact,
    EditRoutingContact,
    AddPricingRate,
    RemovePricingRate,
    EditPricingRate,
    ViewModelSettings,
    EditModelSettings,
    ManageUsers,
}

val ALL_TENANT_PERMISSIONS: List<TenantPermission> = TenantPermission.entries.toList()

@Serializable
data class AccountStatusDto(
    val hasTenant: Boolean,
    val tenantName: String?,
    val isAdmin: Boolean,
    val canBootstrap: Boolean,
)

@Serializable
data class TenantInvitationDto(
    val id: String,
    val email: String,
    val permissions: List<TenantPermission>,
    val status: TenantInvitationStatus,
    val createdAt: String,
    val acceptedAt: String?,
)

@Serializable
data class TenantUserDto(
    val id: String,
    val email: String,
    val name: String,
    val surname: String,
    val isAdmin: Boolean,
    val permissions: List<TenantPermission>,
    val firstLoginAt: String,
)

@Serializable
data class AuditLogEntryDto(
    val id: String,
    val actorEmail: String,
    val action: String,
    val details: String?,
    val timestamp: String,
)
