package org.structured_barebone.packages.tenant.catalog

import org.structured_barebone.packages.tenant.model.PackageEntitlement

/**
 * Resolves only features that are both compiled into the client and enabled by the server.
 * It deliberately cannot download or execute a package at runtime.
 */
class FeatureCatalog(
    private val installedPackages: List<InstalledFeaturePackage>,
) {
    fun enabledPackages(entitlements: List<PackageEntitlement>): List<EnabledFeaturePackage> =
        installedPackages.mapNotNull { installed ->
            val entitlement = entitlements.firstOrNull { it.packageId == installed.packageId }
                ?: return@mapNotNull null
            val features = installed.featureIds.filter { it in entitlement.enabledFeatures }
            if (features.isEmpty()) null else EnabledFeaturePackage(installed, features)
        }
}

data class InstalledFeaturePackage(
    val packageId: String,
    val version: String,
    val featureIds: List<String>,
)

data class EnabledFeaturePackage(
    val packageDefinition: InstalledFeaturePackage,
    val enabledFeatureIds: List<String>,
)
