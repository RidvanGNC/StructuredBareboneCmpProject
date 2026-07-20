package org.structured_barebone.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.structured_barebone.data.settings.AppPreferences
import org.structured_barebone.data.settings.SUPPORTED_LANGUAGE_TAGS
import structuredbarebonecmpproject.shared.generated.resources.Res
import structuredbarebonecmpproject.shared.generated.resources.common_cancel
import structuredbarebonecmpproject.shared.generated.resources.language_name_ar
import structuredbarebonecmpproject.shared.generated.resources.language_name_de
import structuredbarebonecmpproject.shared.generated.resources.language_name_en
import structuredbarebonecmpproject.shared.generated.resources.language_name_ru
import structuredbarebonecmpproject.shared.generated.resources.language_name_tr
import structuredbarebonecmpproject.shared.generated.resources.user_delete_account
import structuredbarebonecmpproject.shared.generated.resources.user_delete_confirm_body
import structuredbarebonecmpproject.shared.generated.resources.user_delete_confirm_title
import structuredbarebonecmpproject.shared.generated.resources.user_deletion_requested
import structuredbarebonecmpproject.shared.generated.resources.user_first_login
import structuredbarebonecmpproject.shared.generated.resources.common_save
import structuredbarebonecmpproject.shared.generated.resources.user_language_section_title
import structuredbarebonecmpproject.shared.generated.resources.user_language_system_default
import structuredbarebonecmpproject.shared.generated.resources.user_last_login
import structuredbarebonecmpproject.shared.generated.resources.user_logout
import structuredbarebonecmpproject.shared.generated.resources.user_server_hint
import structuredbarebonecmpproject.shared.generated.resources.user_server_placeholder
import structuredbarebonecmpproject.shared.generated.resources.user_server_section_title
import org.structured_barebone.viewmodel.UserViewModel

@Composable
fun UserScreen(onLoggedOut: () -> Unit = {}) {
    val viewModel: UserViewModel = koinInject()
    val appPreferences: AppPreferences = koinInject()
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
                                text = stringResource(Res.string.user_first_login, profile.firstLoginAt.toString()),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                            )
                            Text(
                                text = stringResource(Res.string.user_last_login, profile.lastLoginAt.toString()),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                            )
                        }
                    }

                    LanguageSection(appPreferences)
                    ServerAddressSection(appPreferences)

                    OutlinedButton(onClick = onLoggedOut, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(Res.string.user_logout))
                    }

                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(Res.string.user_delete_account))
                    }

                    if (deletionRequested) {
                        Text(
                            text = stringResource(Res.string.user_deletion_requested),
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
            title = { Text(stringResource(Res.string.user_delete_confirm_title)) },
            text = {
                Text(stringResource(Res.string.user_delete_confirm_body))
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteAccount()
                }) {
                    Text(stringResource(Res.string.user_delete_account))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            },
        )
    }
}

@Composable
private fun languageLabel(tag: String?): String = when (tag) {
    null -> stringResource(Res.string.user_language_system_default)
    "tr" -> stringResource(Res.string.language_name_tr)
    "en" -> stringResource(Res.string.language_name_en)
    "ar" -> stringResource(Res.string.language_name_ar)
    "ru" -> stringResource(Res.string.language_name_ru)
    "de" -> stringResource(Res.string.language_name_de)
    else -> tag
}

@Composable
private fun LanguageSection(appPreferences: AppPreferences) {
    val selectedTag by appPreferences.languageTag.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(Res.string.user_language_section_title), style = MaterialTheme.typography.titleMedium)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(languageLabel(selectedTag), modifier = Modifier.fillMaxWidth())
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text(languageLabel(null)) },
                        onClick = {
                            appPreferences.setLanguageTag(null)
                            expanded = false
                        },
                    )
                    SUPPORTED_LANGUAGE_TAGS.forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(languageLabel(tag)) },
                            onClick = {
                                appPreferences.setLanguageTag(tag)
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerAddressSection(appPreferences: AppPreferences) {
    val currentOverride by appPreferences.apiBaseUrlOverride.collectAsState()
    var text by remember(currentOverride) { mutableStateOf(currentOverride ?: "") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(Res.string.user_server_section_title), style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(stringResource(Res.string.user_server_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(Res.string.user_server_hint),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
            Button(onClick = { appPreferences.setApiBaseUrlOverride(text) }) {
                Text(stringResource(Res.string.common_save))
            }
        }
    }
}
