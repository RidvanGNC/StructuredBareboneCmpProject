package org.structured_barebone.cmp_project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import org.structured_barebone.cmp_project.theme.AppTheme
import org.structured_barebone.navigation.SetupNavGraph
import org.structured_barebone.shared.navigation.Screen
import org.structured_barebone.viewmodel.AccountViewModel
import org.structured_barebone.viewmodel.UserViewModel

@Composable
fun App() {
    val accountViewModel: AccountViewModel = koinInject()
    val accountState by accountViewModel.uiState.collectAsState()

    AppTheme {
        when (val state = accountState) {
            is AccountViewModel.UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is AccountViewModel.UiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.message)
            }

            is AccountViewModel.UiState.Success -> {
                if (!state.status.hasTenant) {
                    if (state.status.canBootstrap) {
                        CreateCompanyGate(onCreate = { name -> accountViewModel.createCompany(name) })
                    } else {
                        PendingInviteGate()
                    }
                } else {
                    MainApp()
                }
            }
        }
    }
}

@Composable
private fun CreateCompanyGate(onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Şirketinizi oluşturun", style = MaterialTheme.typography.titleLarge)
            Text(
                "Bu sistemde henüz kayıtlı bir şirket yok. Bir şirket oluşturarak admini olun.",
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Şirket Adı") })
            Button(onClick = { if (name.isNotBlank()) onCreate(name) }) {
                Text("Şirket Oluştur")
            }
        }
    }
}

@Composable
private fun PendingInviteGate() {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Davet bekleniyor", style = MaterialTheme.typography.titleLarge)
            Text(
                "Hesabınız henüz hiçbir şirkete davet edilmemiş. Lütfen şirket adminizle iletişime geçin.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun MainApp() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = koinInject()
    val userState by userViewModel.uiState.collectAsState()
    val isAdmin = (userState as? UserViewModel.UiState.Success)?.profile?.isAdmin == true

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
                if (isAdmin) {
                    NavigationBarItem(
                        selected = currentRoute?.contains("Settings") == true,
                        onClick = { navController.navigate(Screen.Settings) },
                        icon = { Icon(Icons.Default.Build, contentDescription = "Ayarlar") },
                        label = { Text("Ayarlar") },
                    )
                    NavigationBarItem(
                        selected = currentRoute?.contains("Account") == true,
                        onClick = { navController.navigate(Screen.Account) },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Şirket") },
                        label = { Text("Şirket") },
                    )
                }
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
