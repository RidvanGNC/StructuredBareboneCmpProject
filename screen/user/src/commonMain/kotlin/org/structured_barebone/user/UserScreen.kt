package org.structured_barebone.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.structured_barebone.viewmodel.UserViewModel

@Composable
fun UserScreen(onLoggedOut: () -> Unit = {}) {
    val viewModel: UserViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val deletionRequested by viewModel.deletionRequested.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold { padding ->
        when (val state = uiState) {
            is UserViewModel.UiState.Loading -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }

            is UserViewModel.UiState.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(state.message)
            }

            is UserViewModel.UiState.Success -> {
                val profile = state.profile
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${profile.name} ${profile.surname}",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(text = profile.email, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "İlk giriş: ${profile.firstLoginAt}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                            )
                            Text(
                                text = "Son giriş: ${profile.lastLoginAt}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                            )
                        }
                    }

                    OutlinedButton(onClick = onLoggedOut, modifier = Modifier.fillMaxWidth()) {
                        Text("Çıkış Yap")
                    }

                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Hesabı Sil")
                    }

                    if (deletionRequested) {
                        Text(
                            text = "Hesap silme talebiniz alındı.",
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hesabınızı silmek istediğinize emin misiniz?") },
            text = {
                Text(
                    "Bu işlem geri alınamaz. 30 gün içerisinde teknik ekip ile görüşerek " +
                        "hesabınızı yeniden aktive edebilirsiniz."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteAccount()
                }) {
                    Text("Hesabı Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Vazgeç")
                }
            },
        )
    }
}
