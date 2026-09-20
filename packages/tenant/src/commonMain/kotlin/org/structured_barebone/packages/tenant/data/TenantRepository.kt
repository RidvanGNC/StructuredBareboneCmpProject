package org.structured_barebone.packages.tenant.data

import org.structured_barebone.packages.tenant.model.TenantBootstrap

interface TenantRepository {
    suspend fun loadBootstrap(): TenantBootstrap

    /** The server verifies membership and returns the complete refreshed authorization state. */
    suspend fun selectActiveTenant(tenantId: String): TenantBootstrap
}
