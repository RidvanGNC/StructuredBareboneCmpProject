package org.structured_barebone.feature

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FeatureRegistryTest {
    private val reports = FeatureModule("reports", "1.0.0", listOf("read", "export"))
    private val infrastructure = FeatureModule("sync", "1.0.0")

    @Test
    fun everything_is_visible_by_default() {
        val registry = FeatureRegistry(listOf(reports, infrastructure))

        assertEquals(listOf("read", "export"), registry.visibleFeatureIds("reports"))
        assertEquals(listOf(reports, infrastructure), registry.activeModules())
    }

    @Test
    fun a_policy_hides_features_and_modules_without_visible_features() {
        val onlyRead = FeatureVisibilityPolicy { _, featureId -> featureId == "read" }
        val registry = FeatureRegistry(listOf(reports, infrastructure), onlyRead)

        assertEquals(listOf("read"), registry.visibleFeatureIds("reports"))
        assertTrue(registry.isVisible("reports", "read"))
        assertFalse(registry.isVisible("reports", "export"))

        val none = FeatureRegistry(listOf(reports, infrastructure), FeatureVisibilityPolicy { _, _ -> false })
        assertEquals(listOf(infrastructure), none.activeModules())
    }

    @Test
    fun the_policy_is_evaluated_on_every_call() {
        var allowed = false
        val registry = FeatureRegistry(listOf(reports), FeatureVisibilityPolicy { _, _ -> allowed })
        assertTrue(registry.visibleFeatureIds("reports").isEmpty())

        allowed = true

        assertEquals(2, registry.visibleFeatureIds("reports").size)
    }

    @Test
    fun unknown_modules_have_no_features() {
        assertTrue(FeatureRegistry(listOf(reports)).visibleFeatureIds("missing").isEmpty())
    }

    @Test
    fun duplicate_module_ids_are_rejected() {
        assertFailsWith<IllegalArgumentException> { FeatureRegistry(listOf(reports, reports.copy(version = "2"))) }
    }

    @Test
    fun hooks_run_for_every_module_in_order() = runTest {
        val calls = mutableListOf<String>()
        val registry = FeatureRegistry(
            listOf(
                FeatureModule("a", "1", onStartup = { calls += "start-a" }, onSignedIn = { calls += "in-a" }),
                FeatureModule("b", "1", onStartup = { calls += "start-b" }, onSignedIn = { calls += "in-b" }),
            ),
        )

        registry.start()
        registry.signedIn()

        assertEquals(listOf("start-a", "start-b", "in-a", "in-b"), calls)
    }
}
