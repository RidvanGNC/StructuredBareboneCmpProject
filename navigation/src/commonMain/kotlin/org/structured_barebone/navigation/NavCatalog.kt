package org.structured_barebone.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.structured_barebone.feature.FeatureRegistry
import org.structured_barebone.navigation.resources.Res
import org.structured_barebone.navigation.resources.ic_home
import org.structured_barebone.navigation.resources.ic_profile
import org.structured_barebone.navigation.resources.nav_home
import org.structured_barebone.navigation.resources.nav_profile
import org.structured_barebone.navigation.route.Screen

/** One entry of the bottom bar (phones) or side rail (tablets and desktop). */
class NavItem(
    val label: StringResource,
    val icon: DrawableResource,
    /** A [Screen] object or a route string; passed to `NavController.navigate`. */
    val route: Any,
    val isSelected: (NavDestination?) -> Boolean,
)

/**
 * A screen contributed by a compiled-in feature module. It appears only while the registry's visibility policy
 * lets the user see [featureId], and its [route] is a plain string so a feature does not need to edit [Screen].
 */
class FeatureDestination(
    val moduleId: String,
    val featureId: String,
    val route: String,
    val label: StringResource,
    val icon: DrawableResource,
    val content: @Composable () -> Unit,
)

/** The top-level destinations of the base template; features add their own through [FeatureDestination]. */
object NavCatalog {
    val base: List<NavItem> = listOf(
        NavItem(Res.string.nav_home, Res.drawable.ic_home, Screen.Template) { it?.hasRoute<Screen.Template>() == true },
        NavItem(Res.string.nav_profile, Res.drawable.ic_profile, Screen.Profile) { it?.hasRoute<Screen.Profile>() == true },
    )

    fun items(visible: List<FeatureDestination>): List<NavItem> =
        base + visible.map { destination ->
            NavItem(destination.label, destination.icon, destination.route) { it?.route == destination.route }
        }
}

/** The destinations the user may see right now. */
fun visibleDestinations(registry: FeatureRegistry, destinations: List<FeatureDestination>): List<FeatureDestination> =
    destinations.filter { registry.isVisible(it.moduleId, it.featureId) }
