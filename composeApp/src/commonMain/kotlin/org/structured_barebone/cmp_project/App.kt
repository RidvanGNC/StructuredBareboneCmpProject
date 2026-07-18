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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.structured_barebone.cmp_project.theme.AppTheme
import org.structured_barebone.navigation.SetupNavGraph
import org.structured_barebone.shared.navigation.Screen

@Composable
fun App() {
    AppTheme {
        MainApp()
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
                    icon = { Icon(Icons.Default.MailOutline, contentDescription = "Mailler") },
                    label = { Text("Mailler") },
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("RoutingContacts") == true,
                    onClick = { navController.navigate(Screen.RoutingContacts) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Yönlendirmeler") },
                    label = { Text("Yönlendirmeler") },
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("PricingRates") == true,
                    onClick = { navController.navigate(Screen.PricingRates) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Fiyatlandırma") },
                    label = { Text("Fiyatlandırma") },
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("Settings") == true,
                    onClick = { navController.navigate(Screen.Settings) },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Ayarlar") },
                    label = { Text("Ayarlar") },
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("User") == true,
                    onClick = { navController.navigate(Screen.User) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Hesabım") },
                    label = { Text("Hesabım") },
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            SetupNavGraph(navController = navController, startDestination = Screen.Mails)
        }
    }
}
