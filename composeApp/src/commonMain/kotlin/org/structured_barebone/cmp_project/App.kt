package org.structured_barebone.cmp_project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.structured_barebone.cmp_project.theme.AppTheme
import org.structured_barebone.data.settings.AppPreferences
import org.structured_barebone.data.settings.DEFAULT_LANGUAGE_TAG
import org.structured_barebone.data.settings.SUPPORTED_LANGUAGE_TAGS
import org.structured_barebone.navigation.SetupNavGraph
import structuredbarebonecmpproject.shared.generated.resources.Res
import structuredbarebonecmpproject.shared.generated.resources.nav_mails
import structuredbarebonecmpproject.shared.generated.resources.nav_pricing_rates
import structuredbarebonecmpproject.shared.generated.resources.nav_routing_contacts
import structuredbarebonecmpproject.shared.generated.resources.nav_settings
import structuredbarebonecmpproject.shared.generated.resources.nav_user
import org.structured_barebone.shared.navigation.Screen

@Composable
fun App() {
    val appPreferences: AppPreferences = koinInject()
    val storedTag by appPreferences.languageTag.collectAsState()
    val effectiveTag = remember(storedTag) {
        val candidate = storedTag ?: systemLanguageTag()
        if (candidate in SUPPORTED_LANGUAGE_TAGS) candidate else DEFAULT_LANGUAGE_TAG
    }
    // Locale.setDefault/NSUserDefaults yazimi process-global bir yan etkidir; key(effectiveTag)
    // bu deger degistiginde alt agacin tamamen yeniden compose edilmesini (ve stringResource
    // cagrilarinin guncel dili okumasini) garantiler.
    applyLanguageTag(effectiveTag)
    val layoutDirection = if (effectiveTag == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            key(effectiveTag) {
                MainApp()
            }
        }
    }
}

@Composable
private fun MainApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute?.contains("Mails") == true,
                    onClick = { navController.navigate(Screen.Mails) },
                    icon = { Icon(Icons.Default.MailOutline, contentDescription = stringResource(Res.string.nav_mails)) },
                    label = { Text(stringResource(Res.string.nav_mails)) },
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("RoutingContacts") == true,
                    onClick = { navController.navigate(Screen.RoutingContacts) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(Res.string.nav_routing_contacts)) },
                    label = { Text(stringResource(Res.string.nav_routing_contacts)) },
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("PricingRates") == true,
                    onClick = { navController.navigate(Screen.PricingRates) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(Res.string.nav_pricing_rates)) },
                    label = { Text(stringResource(Res.string.nav_pricing_rates)) },
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("Settings") == true,
                    onClick = { navController.navigate(Screen.Settings) },
                    icon = { Icon(Icons.Default.Build, contentDescription = stringResource(Res.string.nav_settings)) },
                    label = { Text(stringResource(Res.string.nav_settings)) },
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("User") == true,
                    onClick = { navController.navigate(Screen.User) },
                    icon = { Icon(Icons.Default.Person, contentDescription = stringResource(Res.string.nav_user)) },
                    label = { Text(stringResource(Res.string.nav_user)) },
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            SetupNavGraph(navController = navController, startDestination = Screen.Mails)
        }
    }
}
