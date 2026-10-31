package org.structured_barebone.packages.tenant.catalog

import kotlinx.coroutines.flow.MutableStateFlow
import org.structured_barebone.feature.FeatureModule
import org.structured_barebone.feature.FeatureRegistry
import org.structured_barebone.packages.tenant.model.PackageEntitlement
import org.structured_barebone.packages.tenant.model.TenantBootstrap
import org.structured_barebone.packages.tenant.model.TenantMode
import org.structured_barebone.packages.tenant.runtime.TenantSessionState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EntitlementVisibilityPolicyTest {
    private fun ready(vararg entitlements: PackageEntitlement) = TenantSessionState.Ready(
        TenantBootstrap(
            tenantMode = TenantMode.MULTI_TENANT,
            subjectId = "subject",
            memberships = emptyList(),
            packageEntitlements = entitlements.toList(),
            authorizationVersion = "1",
        ),
    )

    @Test
    fun only_features_granted_by_the_server_are_visible() {
        val entitlements = listOf(PackageEntitlement("reports", "1.0.0", listOf("read", "unknown")))

        assertTrue(isEntitled(entitlements, "reports", "read"))
        assertFalse(isEntitled(entitlements, "reports", "export"))
        assertFalse(isEntitled(entitlements, "other", "read"))
    }

    @Test
    fun nothing_is_visible_until_the_tenant_context_is_loaded() {
        val state = MutableStateFlow<TenantSessionState>(TenantSessionState.Idle)
        val policy = EntitlementVisibilityPolicy(state)

        assertFalse(policy.isVisible("reports", "read"))

        state.value = ready(PackageEntitlement("reports", "1.0.0", listOf("read")))

        assertTrue(policy.isVisible("reports", "read"))
        assertFalse(policy.isVisible("reports", "export"))
    }

    @Test
    fun the_registry_shows_the_compiled_features_the_server_enabled() {
        val state = MutableStateFlow(ready(PackageEntitlement("reports", "1.0.0", listOf("read", "unknown"))))
        val registry = FeatureRegistry(
            listOf(FeatureModule("reports", "1.0.0", listOf("read", "export"))),
            EntitlementVisibilityPolicy(state),
        )

        assertEquals(listOf("read"), registry.visibleFeatureIds("reports"))
    }
}
