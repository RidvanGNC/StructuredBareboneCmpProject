package org.structured_barebone.packages.tenant.model

import kotlinx.serialization.Serializable

/** A server-issued tenant selector. It is never authorization proof on its own. */
@Serializable
data class TenantRef(
    val id: String,
    val displayName: String,
)

@Serializable
enum class TenantMode {
    SINGLE_TENANT,
    MULTI_TENANT,
}

@Serializable
data class TenantMembership(
    val tenant: TenantRef,
    val roles: List<String>,
)

/** A package is compiled into the client; this entitlement only enables its already-known features. */
@Serializable
data class PackageEntitlement(
    val packageId: String,
    val packageVersion: String,
    val enabledFeatures: List<String>,
)

@Serializable
data class TenantBootstrap(
    val tenantMode: TenantMode,
    val subjectId: String,
    val activeTenant: TenantRef? = null,
    val memberships: List<TenantMembership>,
    val packageEntitlements: List<PackageEntitlement>,
    val authorizationVersion: String,
)
