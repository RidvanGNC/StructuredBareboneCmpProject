package org.structured_barebone.feature

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.module.Module

/**
 * A capability compiled into the app: its identity, the features it offers, the Koin modules it needs and
 * start-up hooks. Nothing here is downloaded or executed at run time; the server can only change which
 * compiled features are visible (see [FeatureVisibilityPolicy]).
 *
 * @property featureIds the user-facing features of the module. A module without any is infrastructure and is
 * always active.
 */
data class FeatureModule(
    val id: String,
    val version: String,
    val featureIds: List<String> = emptyList(),
    val koinModules: List<Module> = emptyList(),
    val onStartup: suspend () -> Unit = {},
    val onSignedIn: suspend () -> Unit = {},
)

/**
 * Decides which compiled features the user may see. The base template shows everything ([AllFeaturesVisible]);
 * an entitlement-controlled product replaces it, for example with the one in the optional tenant package.
 * The decision is evaluated on every call, so a policy backed by changing state stays current; such a policy
 * also emits on [changes] so the UI can read it again.
 */
fun interface FeatureVisibilityPolicy {
    fun isVisible(moduleId: String, featureId: String): Boolean

    /** Emits whenever [isVisible] may have started to answer differently. Static policies never emit. */
    val changes: Flow<Unit> get() = emptyFlow()
}

object AllFeaturesVisible : FeatureVisibilityPolicy {
    override fun isVisible(moduleId: String, featureId: String): Boolean = true
}

/** The compiled-in feature modules and what the current policy lets the user see. */
class FeatureRegistry(
    val modules: List<FeatureModule>,
    private val policy: FeatureVisibilityPolicy = AllFeaturesVisible,
) {
    init {
        val duplicates = modules.groupBy { it.id }.filterValues { it.size > 1 }.keys
        require(duplicates.isEmpty()) { "Feature module ids must be unique: $duplicates" }
    }

    /** See [FeatureVisibilityPolicy.changes]. */
    val changes: Flow<Unit> get() = policy.changes

    fun isVisible(moduleId: String, featureId: String): Boolean = policy.isVisible(moduleId, featureId)

    fun visibleFeatureIds(moduleId: String): List<String> =
        modules.firstOrNull { it.id == moduleId }?.featureIds.orEmpty().filter { policy.isVisible(moduleId, it) }

    /** Modules that are infrastructure or offer at least one visible feature. */
    fun activeModules(): List<FeatureModule> =
        modules.filter { it.featureIds.isEmpty() || visibleFeatureIds(it.id).isNotEmpty() }

    suspend fun start() = modules.forEach { it.onStartup() }

    suspend fun signedIn() = modules.forEach { it.onSignedIn() }
}
