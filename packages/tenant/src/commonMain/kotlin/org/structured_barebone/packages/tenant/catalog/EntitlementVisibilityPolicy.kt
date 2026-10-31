package org.structured_barebone.packages.tenant.catalog

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.structured_barebone.feature.FeatureVisibilityPolicy
import org.structured_barebone.packages.tenant.model.PackageEntitlement
import org.structured_barebone.packages.tenant.runtime.TenantSessionState

/** True when the server granted [featureId] of the compiled-in module [moduleId]. */
fun isEntitled(entitlements: List<PackageEntitlement>, moduleId: String, featureId: String): Boolean =
    entitlements.any { it.packageId == moduleId && featureId in it.enabledFeatures }

/**
 * Shows only features that are both compiled into the client (a `FeatureModule`) and enabled by the server for
 * the active tenant. Until the tenant context is loaded nothing is shown. It cannot download or execute a package.
 */
class EntitlementVisibilityPolicy(
    private val tenantState: StateFlow<TenantSessionState>,
) : FeatureVisibilityPolicy {
    override val changes: Flow<Unit> = tenantState.map { }

    override fun isVisible(moduleId: String, featureId: String): Boolean {
        val ready = tenantState.value as? TenantSessionState.Ready ?: return false
        return isEntitled(ready.bootstrap.packageEntitlements, moduleId, featureId)
    }
}
