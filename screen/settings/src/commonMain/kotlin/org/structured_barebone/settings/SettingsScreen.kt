package org.structured_barebone.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import structuredbarebonecmpproject.shared.generated.resources.Res
import structuredbarebonecmpproject.shared.generated.resources.common_delete_cd
import structuredbarebonecmpproject.shared.generated.resources.common_save
import structuredbarebonecmpproject.shared.generated.resources.settings_api_key_label
import structuredbarebonecmpproject.shared.generated.resources.settings_cloud_provider_label
import structuredbarebonecmpproject.shared.generated.resources.settings_cloud_tab
import structuredbarebonecmpproject.shared.generated.resources.settings_engine_tab
import structuredbarebonecmpproject.shared.generated.resources.settings_local_model_label
import structuredbarebonecmpproject.shared.generated.resources.settings_log_failed
import structuredbarebonecmpproject.shared.generated.resources.settings_log_success
import structuredbarebonecmpproject.shared.generated.resources.settings_log_tokens_duration
import structuredbarebonecmpproject.shared.generated.resources.settings_logs_tab
import structuredbarebonecmpproject.shared.generated.resources.settings_max_tokens_label
import structuredbarebonecmpproject.shared.generated.resources.settings_mode_cloud
import structuredbarebonecmpproject.shared.generated.resources.settings_mode_label
import structuredbarebonecmpproject.shared.generated.resources.settings_mode_local
import structuredbarebonecmpproject.shared.generated.resources.settings_model_size_gb
import structuredbarebonecmpproject.shared.generated.resources.settings_models_tab
import structuredbarebonecmpproject.shared.generated.resources.settings_not_configured
import structuredbarebonecmpproject.shared.generated.resources.settings_temperature_label
import structuredbarebonecmpproject.shared.generated.resources.settings_title
import structuredbarebonecmpproject.shared.generated.resources.settings_top_p_label
import org.structured_barebone.shared.model.CloudCredentialDto
import org.structured_barebone.shared.model.CloudProvider
import org.structured_barebone.shared.model.EngineMode
import org.structured_barebone.shared.model.EngineSettingsDto
import org.structured_barebone.shared.model.LlmRequestLogDto
import org.structured_barebone.shared.model.LocalModelDto
import org.structured_barebone.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.settings_title)) }) },
    ) { padding ->
        when (val state = uiState) {
            is SettingsViewModel.UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is SettingsViewModel.UiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.message)
            }

            is SettingsViewModel.UiState.Success -> Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(stringResource(Res.string.settings_engine_tab)) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(stringResource(Res.string.settings_models_tab)) })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(stringResource(Res.string.settings_cloud_tab)) })
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text(stringResource(Res.string.settings_logs_tab)) })
                }

                when (selectedTab) {
                    0 -> EngineConfigTab(state.engineSettings, state.localModels, viewModel)
                    1 -> LocalModelsTab(state.localModels)
                    2 -> CloudCredentialsTab(state.cloudCredentials, viewModel)
                    else -> LogsTab(state.logs)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EngineConfigTab(
    settings: EngineSettingsDto,
    localModels: List<LocalModelDto>,
    viewModel: SettingsViewModel,
) {
    var mode by remember(settings.id) { mutableStateOf(settings.mode) }
    var localModelName by remember(settings.id) { mutableStateOf(settings.localModelName) }
    var cloudProvider by remember(settings.id) { mutableStateOf(settings.cloudProvider) }
    var cloudApiKey by remember(settings.id) { mutableStateOf("") }
    var temperature by remember(settings.id) { mutableStateOf(settings.temperature.toString()) }
    var maxTokens by remember(settings.id) { mutableStateOf(settings.maxTokens?.toString() ?: "") }
    var topP by remember(settings.id) { mutableStateOf(settings.topP?.toString() ?: "") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnumDropdown(
            label = stringResource(Res.string.settings_mode_label),
            options = EngineMode.entries,
            selected = mode,
            optionLabel = { if (it == EngineMode.Local) stringResource(Res.string.settings_mode_local) else stringResource(Res.string.settings_mode_cloud) },
            onSelect = { mode = it },
        )

        if (mode == EngineMode.Local) {
            EnumDropdown(
                label = stringResource(Res.string.settings_local_model_label),
                options = localModels.map { it.name },
                selected = localModelName,
                optionLabel = { it },
                onSelect = { localModelName = it },
            )
        } else {
            EnumDropdown(
                label = stringResource(Res.string.settings_cloud_provider_label),
                options = CloudProvider.entries,
                selected = cloudProvider,
                optionLabel = { it.name },
                onSelect = { cloudProvider = it },
            )
            OutlinedTextField(
                value = cloudApiKey,
                onValueChange = { cloudApiKey = it },
                label = { Text(stringResource(Res.string.settings_api_key_label)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        OutlinedTextField(
            value = temperature,
            onValueChange = { temperature = it },
            label = { Text(stringResource(Res.string.settings_temperature_label)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = maxTokens,
            onValueChange = { maxTokens = it },
            label = { Text(stringResource(Res.string.settings_max_tokens_label)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = topP,
            onValueChange = { topP = it },
            label = { Text(stringResource(Res.string.settings_top_p_label)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = {
                viewModel.updateEngineConfig(
                    mode = mode,
                    localModelName = localModelName.ifBlank { null },
                    cloudProvider = if (mode == EngineMode.Cloud) cloudProvider else null,
                    cloudApiKey = cloudApiKey.ifBlank { null },
                    temperature = temperature.toDoubleOrNull() ?: 0.0,
                    maxTokens = maxTokens.toIntOrNull(),
                    topP = topP.toDoubleOrNull(),
                )
            },
        ) {
            Text(stringResource(Res.string.common_save))
        }
    }
}

@Composable
private fun <T> EnumDropdown(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(optionLabel(selected), modifier = Modifier.fillMaxWidth())
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalModelsTab(models: List<LocalModelDto>) {
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(models) { model ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(model.name, style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(Res.string.settings_model_size_gb, (model.sizeBytes / 1024 / 1024 / 1024).toString()))
                }
            }
        }
    }
}

@Composable
private fun CloudCredentialsTab(credentials: List<CloudCredentialDto>, viewModel: SettingsViewModel) {
    val credentialByProvider = credentials.associateBy { it.provider }

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(CloudProvider.entries) { provider ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(provider.name, style = MaterialTheme.typography.titleMedium)
                        Text(credentialByProvider[provider]?.maskedPreview ?: stringResource(Res.string.settings_not_configured))
                    }
                    if (credentialByProvider.containsKey(provider)) {
                        IconButton(onClick = { viewModel.deleteCloudCredential(provider) }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.common_delete_cd))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogsTab(logs: List<LlmRequestLogDto>) {
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(logs) { log ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${log.modelName} · ${log.requestType}", style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(Res.string.settings_log_tokens_duration, log.totalTokens?.toString() ?: "-", log.durationMs.toString()))
                    Text(if (log.success) stringResource(Res.string.settings_log_success) else stringResource(Res.string.settings_log_failed, log.errorMessage ?: ""))
                }
            }
        }
    }
}
