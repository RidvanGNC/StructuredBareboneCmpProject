package org.structured_barebone.packages.tenant.catalog

import kotlin.test.Test
import kotlin.test.assertEquals
import org.structured_barebone.packages.tenant.model.PackageEntitlement

class FeatureCatalogTest {
    @Test
    fun `enables only installed features granted by the server`() {
        val catalog = FeatureCatalog(
            listOf(InstalledFeaturePackage("reports", "1.0.0", listOf("read", "export"))),
        )

        val enabled = catalog.enabledPackages(
            listOf(PackageEntitlement("reports", "1.0.0", listOf("read", "unknown"))),
        )

        assertEquals(listOf("read"), enabled.single().enabledFeatureIds)
    }
}
