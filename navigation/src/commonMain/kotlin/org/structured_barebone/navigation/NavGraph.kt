package org.structured_barebone.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.structured_barebone.feature.FeatureRegistry
import org.structured_barebone.navigation.route.Screen
import org.structured_barebone.profile.ProfileScreen
import org.structured_barebone.template.TemplateScreen
import org.structured_barebone.theme.LayoutSize
import org.structured_barebone.theme.LocalLayoutSize

/**
 * The signed-in app: a bottom bar on phones, a side rail on wider windows, and the destinations of the base
 * template plus the visible [featureDestinations].
 */
@Composable
fun SetupNavGraph(
    featureRegistry: FeatureRegistry,
    onSignOut: () -> Unit,
    featureDestinations: List<FeatureDestination> = emptyList(),
    startDestination: Screen = Screen.Template,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Reading the policy again whenever it reports a change keeps the menu current.
    var visibilityRevision by remember { mutableIntStateOf(0) }
    LaunchedEffect(featureRegistry) { featureRegistry.changes.collect { visibilityRevision++ } }
    val visible = remember(featureRegistry, featureDestinations, visibilityRevision) {
        visibleDestinations(featureRegistry, featureDestinations)
    }
    val items = remember(visible) { NavCatalog.items(visible) }
    val current = backStackEntry?.destination

    val host: @Composable (Modifier) -> Unit = { modifier ->
        NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
            composable<Screen.Template> { TemplateScreen() }
            composable<Screen.Profile> { ProfileScreen(onSignOut = onSignOut) }
            featureDestinations.forEach { destination ->
                composable(destination.route) {
                    if (featureRegistry.isVisible(destination.moduleId, destination.featureId)) destination.content()
                }
            }
        }
    }

    if (LocalLayoutSize.current == LayoutSize.Compact) {
        Column(modifier = Modifier.fillMaxSize()) {
            host(Modifier.weight(1f))
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = item.isSelected(current),
                        onClick = { navController.openTopLevel(item) },
                        icon = { Icon(painterResource(item.icon), contentDescription = null) },
                        label = { Text(stringResource(item.label)) },
                    )
                }
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail {
                items.forEach { item ->
                    NavigationRailItem(
                        selected = item.isSelected(current),
                        onClick = { navController.openTopLevel(item) },
                        icon = { Icon(painterResource(item.icon), contentDescription = null) },
                        label = { Text(stringResource(item.label)) },
                    )
                }
            }
            host(Modifier.weight(1f))
        }
    }
}

private fun NavController.openTopLevel(item: NavItem) {
    val route = item.route
    val options: androidx.navigation.NavOptionsBuilder.() -> Unit = {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
    if (route is String) navigate(route, builder = options) else navigate(route, builder = options)
}
