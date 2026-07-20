package org.structured_barebone.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import structuredbarebonecmpproject.shared.generated.resources.Res
import structuredbarebonecmpproject.shared.generated.resources.common_delete_cd
import structuredbarebonecmpproject.shared.generated.resources.common_save
import structuredbarebonecmpproject.shared.generated.resources.settings_api_key_label
import structuredbarebonecmpproject.shared.generated.resources.settings_appearance_brand_text_label
import structuredbarebonecmpproject.shared.generated.resources.settings_appearance_font_family_label
import structuredbarebonecmpproject.shared.generated.resources.settings_appearance_font_scale_label
import structuredbarebonecmpproject.shared.generated.resources.settings_appearance_primary_color_label
import structuredbarebonecmpproject.shared.generated.resources.settings_appearance_reset_color
import structuredbarebonecmpproject.shared.generated.resources.settings_appearance_tab
import structuredbarebonecmpproject.shared.generated.resources.settings_cloud_provider_label
import structuredbarebonecmpproject.shared.generated.resources.settings_cloud_tab
import structuredbarebonecmpproject.shared.generated.resources.settings_engine_tab
import structuredbarebonecmpproject.shared.generated.resources.mail_type_inquiry_or_offer
import structuredbarebonecmpproject.shared.generated.resources.mail_type_translation
import structuredbarebonecmpproject.shared.generated.resources.settings_local_model_label
import structuredbarebonecmpproject.shared.generated.resources.settings_log_failed
import structuredbarebonecmpproject.shared.generated.resources.settings_log_filter_all
import structuredbarebonecmpproject.shared.generated.resources.settings_log_filter_failed
import structuredbarebonecmpproject.shared.generated.resources.settings_log_filter_success
import structuredbarebonecmpproject.shared.generated.resources.settings_log_next_page
import structuredbarebonecmpproject.shared.generated.resources.settings_log_page_indicator
import structuredbarebonecmpproject.shared.generated.resources.settings_log_prev_page
import structuredbarebonecmpproject.shared.generated.resources.settings_log_status_filter_label
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
import structuredbarebonecmpproject.shared.generated.resources.settings_top_k_label
import structuredbarebonecmpproject.shared.generated.resources.settings_top_p_label
import structuredbarebonecmpproject.shared.generated.resources.settings_vacation_enabled_label
import structuredbarebonecmpproject.shared.generated.resources.settings_vacation_message_label
import structuredbarebonecmpproject.shared.generated.resources.settings_vacation_notify_types_label
import structuredbarebonecmpproject.shared.generated.resources.settings_vacation_tab
import org.structured_barebone.data.domain.LlmLogFilter
import org.structured_barebone.shared.model.AppearanceSettingsDto
import org.structured_barebone.shared.model.CloudCredentialDto
import org.structured_barebone.shared.model.CloudProvider
import org.structured_barebone.shared.model.EngineMode
import org.structured_barebone.shared.model.EngineSettingsDto
import org.structured_barebone.shared.model.LlmRequestLogDto
import org.structured_barebone.shared.model.LocalModelDto
import org.structured_barebone.shared.model.MailType
import org.structured_barebone.shared.model.VacationModeSettingsDto
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
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(stringResource(Res.string.settings_appearance_tab)) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(stringResource(Res.string.settings_engine_tab)) })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(stringResource(Res.string.settings_models_tab)) })
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text(stringResource(Res.string.settings_cloud_tab)) })
                    Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text(stringResource(Res.string.settings_vacation_tab)) })
                    Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }, text = { Text(stringResource(Res.string.settings_logs_tab)) })
                }

                when (selectedTab) {
                    0 -> AppearanceTab(state.appearanceSettings, viewModel)
                    1 -> EngineConfigTab(state.engineSettings, state.localModels, viewModel)
                    2 -> LocalModelsTab(state.localModels)
                    3 -> CloudCredentialsTab(state.cloudCredentials, viewModel)
                    4 -> VacationModeTab(state.vacationModeSettings, viewModel)
                    else -> LogsTab(viewModel)
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
    var topK by remember(settings.id) { mutableStateOf(settings.topK?.toString() ?: "") }

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
        OutlinedTextField(
            value = topK,
            onValueChange = { topK = it },
            label = { Text(stringResource(Res.string.settings_top_k_label)) },
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
                    topK = topK.toIntOrNull(),
                )
            },
        ) {
            Text(stringResource(Res.string.common_save))
        }
    }
}

private val PRESET_COLORS = listOf(
    Color(0xFF5B7FDB), Color(0xFF52C41A), Color(0xFFFAAD14), Color(0xFFFF4D4F),
    Color(0xFF722ED1), Color(0xFF13C2C2), Color(0xFFEB2F96), Color(0xFF595959),
)
private const val DEFAULT_PRIMARY_COLOR = "#5B7FDB"
private val FONT_OPTIONS = listOf("Open Sans", "Roboto", "Inter", "Lato", "Montserrat", "Poppins")
private val FONT_SCALE_OPTIONS = listOf(1.0, 1.25, 1.5, 2.0)
private const val BRAND_TEXT_MAX_LENGTH = 50

private fun colorToHex(color: Color): String {
    val r = (color.red * 255).toInt()
    val g = (color.green * 255).toInt()
    val b = (color.blue * 255).toInt()
    return "#" + listOf(r, g, b).joinToString("") { it.toString(16).padStart(2, '0') }
}

@Composable
private fun AppearanceTab(settings: AppearanceSettingsDto, viewModel: SettingsViewModel) {
    var brandText by remember(settings.id) { mutableStateOf(settings.brandText ?: "") }
    var primaryColorHex by remember(settings.id) { mutableStateOf(settings.primaryColor) }
    var fontFamily by remember(settings.id) { mutableStateOf(settings.fontFamily) }
    var fontScale by remember(settings.id) { mutableStateOf(settings.fontScale) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = brandText,
            onValueChange = { if (it.length <= BRAND_TEXT_MAX_LENGTH) brandText = it },
            label = { Text(stringResource(Res.string.settings_appearance_brand_text_label)) },
            supportingText = { Text("${brandText.length}/$BRAND_TEXT_MAX_LENGTH") },
            modifier = Modifier.fillMaxWidth(),
        )

        Text(stringResource(Res.string.settings_appearance_primary_color_label), style = MaterialTheme.typography.labelMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PRESET_COLORS) { color ->
                val hex = colorToHex(color)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color, CircleShape)
                        .border(
                            width = if (primaryColorHex.equals(hex, ignoreCase = true)) 3.dp else 0.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape,
                        )
                        .clickable { primaryColorHex = hex },
                    contentAlignment = Alignment.Center,
                ) {
                    if (primaryColorHex.equals(hex, ignoreCase = true)) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
        OutlinedButton(onClick = { primaryColorHex = DEFAULT_PRIMARY_COLOR }) {
            Text(stringResource(Res.string.settings_appearance_reset_color))
        }

        EnumDropdown(
            label = stringResource(Res.string.settings_appearance_font_family_label),
            options = FONT_OPTIONS,
            selected = fontFamily,
            optionLabel = { it },
            onSelect = { fontFamily = it },
        )

        EnumDropdown(
            label = stringResource(Res.string.settings_appearance_font_scale_label),
            options = FONT_SCALE_OPTIONS,
            selected = fontScale,
            optionLabel = { "${it}x" },
            onSelect = { fontScale = it },
        )

        Button(
            onClick = {
                viewModel.updateAppearance(
                    brandText = brandText.ifBlank { null },
                    brandLogoBase64 = settings.brandLogoBase64,
                    primaryColor = primaryColorHex,
                    fontFamily = fontFamily,
                    fontScale = fontScale,
                )
            },
        ) {
            Text(stringResource(Res.string.common_save))
        }
    }
}

@Composable
private fun VacationModeTab(settings: VacationModeSettingsDto, viewModel: SettingsViewModel) {
    var isEnabled by remember(settings.id) { mutableStateOf(settings.isEnabled) }
    var messageTemplate by remember(settings.id) { mutableStateOf(settings.messageTemplate) }
    var notifyTypes by remember(settings.id) { mutableStateOf(settings.notifyMailTypes.toSet()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.settings_vacation_enabled_label), modifier = Modifier.weight(1f))
            Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
        }

        OutlinedTextField(
            value = messageTemplate,
            onValueChange = { messageTemplate = it },
            label = { Text(stringResource(Res.string.settings_vacation_message_label)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5,
        )

        Text(stringResource(Res.string.settings_vacation_notify_types_label), style = MaterialTheme.typography.labelMedium)
        listOf(MailType.InquiryOrOffer, MailType.Translation).forEach { type ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = type in notifyTypes,
                    onCheckedChange = { checked ->
                        notifyTypes = if (checked) notifyTypes + type else notifyTypes - type
                    },
                )
                Text(vacationMailTypeLabel(type))
            }
        }

        Button(
            onClick = {
                viewModel.updateVacationMode(isEnabled, messageTemplate, notifyTypes.toList())
            },
        ) {
            Text(stringResource(Res.string.common_save))
        }
    }
}

@Composable
private fun vacationMailTypeLabel(type: MailType): String = when (type) {
    MailType.InquiryOrOffer -> stringResource(Res.string.mail_type_inquiry_or_offer)
    MailType.Translation -> stringResource(Res.string.mail_type_translation)
    else -> type.name
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

private const val LOGS_PAGE_SIZE = 20

@Composable
private fun LogsTab(viewModel: SettingsViewModel) {
    val logsState by viewModel.logsState.collectAsState()
    var page by remember { mutableStateOf(1) }
    var successFilter by remember { mutableStateOf<Boolean?>(null) }

    fun reload() {
        viewModel.loadLogs(LlmLogFilter(page = page, pageSize = LOGS_PAGE_SIZE, success = successFilter))
    }

    val allLabel = stringResource(Res.string.settings_log_filter_all)
    val successLabel = stringResource(Res.string.settings_log_filter_success)
    val failedLabel = stringResource(Res.string.settings_log_filter_failed)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                EnumDropdown(
                    label = stringResource(Res.string.settings_log_status_filter_label),
                    options = listOf(null, true, false),
                    selected = successFilter,
                    optionLabel = {
                        when (it) {
                            null -> allLabel
                            true -> successLabel
                            false -> failedLabel
                        }
                    },
                    onSelect = {
                        successFilter = it
                        page = 1
                        reload()
                    },
                )
            }
        }

        when (val state = logsState) {
            is SettingsViewModel.LogsUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is SettingsViewModel.LogsUiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.message)
            }

            is SettingsViewModel.LogsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.page.items) { log -> LogCard(log) }
                }

                val totalPages = ((state.page.totalCount - 1) / LOGS_PAGE_SIZE + 1).coerceAtLeast(1)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        enabled = page > 1,
                        onClick = { page -= 1; reload() },
                    ) {
                        Text(stringResource(Res.string.settings_log_prev_page))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(Res.string.settings_log_page_indicator, page.toString(), totalPages.toString()))
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedButton(
                        enabled = page < totalPages,
                        onClick = { page += 1; reload() },
                    ) {
                        Text(stringResource(Res.string.settings_log_next_page))
                    }
                }
            }
        }
    }
}

@Composable
private fun LogCard(log: LlmRequestLogDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("${log.modelName} · ${log.requestType}", style = MaterialTheme.typography.titleMedium)
            Text(stringResource(Res.string.settings_log_tokens_duration, log.totalTokens?.toString() ?: "-", log.durationMs.toString()))
            Text(if (log.success) stringResource(Res.string.settings_log_success) else stringResource(Res.string.settings_log_failed, log.errorMessage ?: ""))
        }
    }
}
