package org.structured_barebone.navigation

import org.structured_barebone.feature.FeatureModule
import org.structured_barebone.feature.FeatureRegistry
import org.structured_barebone.feature.FeatureVisibilityPolicy
import org.structured_barebone.navigation.resources.Res
import org.structured_barebone.navigation.resources.ic_home
import org.structured_barebone.navigation.resources.nav_home
import kotlin.test.Test
import kotlin.test.assertEquals

class NavCatalogTest {
    private fun destination(feature: String) =
        FeatureDestination("reports", feature, "reports/$feature", Res.string.nav_home, Res.drawable.ic_home) {}

    private val destinations = listOf(destination("read"), destination("export"))
    private val modules = listOf(FeatureModule("reports", "1.0.0", listOf("read", "export")))

    @Test
    fun only_destinations_the_policy_allows_are_listed() {
        val registry = FeatureRegistry(modules, FeatureVisibilityPolicy { _, featureId -> featureId == "read" })

        assertEquals(listOf("reports/read"), visibleDestinations(registry, destinations).map { it.route })
        assertEquals(NavCatalog.base.size + 1, NavCatalog.items(visibleDestinations(registry, destinations)).size)
    }

    @Test
    fun the_base_catalog_has_the_template_destinations_when_no_feature_is_installed() {
        val registry = FeatureRegistry(emptyList())

        assertEquals(NavCatalog.base.size, NavCatalog.items(visibleDestinations(registry, emptyList())).size)
    }
}
