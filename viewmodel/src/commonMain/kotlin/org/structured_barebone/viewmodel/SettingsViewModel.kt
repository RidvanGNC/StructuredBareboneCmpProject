package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.LlmLogFilter
import org.structured_barebone.data.domain.SettingsRepository
import org.structured_barebone.shared.model.AppearanceSettingsDto
import org.structured_barebone.shared.model.CloudCredentialDto
import org.structured_barebone.shared.model.CloudProvider
import org.structured_barebone.shared.model.EngineMode
import org.structured_barebone.shared.model.EngineSettingsDto
import org.structured_barebone.shared.model.LlmLogPageDto
import org.structured_barebone.shared.model.LocalModelDto
import org.structured_barebone.shared.model.MailType
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.VacationModeSettingsDto

class SettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    sealed class UiState {
        data object Loading : UiState()
        data class Success(
            val engineSettings: EngineSettingsDto,
            val localModels: List<LocalModelDto>,
            val cloudCredentials: List<CloudCredentialDto>,
            val appearanceSettings: AppearanceSettingsDto,
            val vacationModeSettings: VacationModeSettingsDto,
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class LogsUiState {
        data object Loading : LogsUiState()
        data class Success(val page: LlmLogPageDto) : LogsUiState()
        data class Error(val message: String) : LogsUiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _logsState = MutableStateFlow<LogsUiState>(LogsUiState.Loading)
    val logsState: StateFlow<LogsUiState> = _logsState.asStateFlow()

    init {
        load()
        loadLogs()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val settingsResult = repository.getEngineConfig()
            val modelsResult = repository.getLocalModels()
            val credentialsResult = repository.getCloudCredentials()
            val appearanceResult = repository.getAppearance()
            val vacationResult = repository.getVacationMode()

            if (settingsResult is Result.Success &&
                modelsResult is Result.Success &&
                credentialsResult is Result.Success &&
                appearanceResult is Result.Success &&
                vacationResult is Result.Success
            ) {
                _uiState.value = UiState.Success(
                    settingsResult.data, modelsResult.data, credentialsResult.data,
                    appearanceResult.data, vacationResult.data,
                )
            } else {
                val message = listOf(settingsResult, modelsResult, credentialsResult, appearanceResult, vacationResult)
                    .filterIsInstance<Result.Error>()
                    .firstOrNull()?.message ?: "Ayarlar yuklenemedi."
                _uiState.value = UiState.Error(message)
            }
        }
    }

    fun loadLogs(filter: LlmLogFilter = LlmLogFilter()) {
        viewModelScope.launch {
            _logsState.value = LogsUiState.Loading
            when (val result = repository.getLogs(filter)) {
                is Result.Success -> _logsState.value = LogsUiState.Success(result.data)
                is Result.Error -> _logsState.value = LogsUiState.Error(result.message)
            }
        }
    }

    fun updateEngineConfig(
        mode: EngineMode,
        localModelName: String?,
        cloudProvider: CloudProvider?,
        cloudApiKey: String?,
        temperature: Double,
        maxTokens: Int?,
        topP: Double?,
        topK: Int?,
    ) {
        viewModelScope.launch {
            val result = repository.updateEngineConfig(
                mode, localModelName, cloudProvider, cloudApiKey, temperature, maxTokens, topP, topK,
            )
            if (result is Result.Success) load()
        }
    }

    fun upsertCloudCredential(provider: CloudProvider, apiKey: String) {
        viewModelScope.launch {
            val result = repository.upsertCloudCredential(provider, apiKey)
            if (result is Result.Success) load()
        }
    }

    fun deleteCloudCredential(provider: CloudProvider) {
        viewModelScope.launch {
            val result = repository.deleteCloudCredential(provider)
            if (result is Result.Success) load()
        }
    }

    fun updateAppearance(
        brandText: String?,
        brandLogoBase64: String?,
        primaryColor: String,
        fontFamily: String,
        fontScale: Double,
    ) {
        viewModelScope.launch {
            val result = repository.updateAppearance(brandText, brandLogoBase64, primaryColor, fontFamily, fontScale)
            if (result is Result.Success) load()
        }
    }

    fun updateVacationMode(isEnabled: Boolean, messageTemplate: String, notifyMailTypes: List<MailType>) {
        viewModelScope.launch {
            val result = repository.updateVacationMode(isEnabled, messageTemplate, notifyMailTypes)
            if (result is Result.Success) load()
        }
    }
}
