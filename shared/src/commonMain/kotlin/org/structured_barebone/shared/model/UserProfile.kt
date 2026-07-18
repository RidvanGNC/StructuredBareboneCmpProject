package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val name: String,
    val surname: String,
    val phoneNumber: String?,
    val firstLoginAt: String,
    val lastLoginAt: String,
    val isActive: Boolean,
    val deletionScheduledAt: String?,
    val language: String,
    val isAdmin: Boolean,
    val tenantId: String?,
    val tenantName: String?,
    val permissions: List<TenantPermission>,
)
