package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.SettingsRepository
import org.structured_barebone.shared.model.CloudCredentialDto
import org.structured_barebone.shared.model.CloudProvider
import org.structured_barebone.shared.model.EngineMode
import org.structured_barebone.shared.model.EngineSettingsDto
import org.structured_barebone.shared.model.LlmRequestLogDto
import org.structured_barebone.shared.model.LocalModelDto
import org.structured_barebone.shared.model.Result

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
            val logs: List<LlmRequestLogDto>,
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val settingsResult = repository.getEngineConfig()
            val modelsResult = repository.getLocalModels()
            val credentialsResult = repository.getCloudCredentials()
            val logsResult = repository.getLogs()

            if (settingsResult is Result.Success &&
                modelsResult is Result.Success &&
                credentialsResult is Result.Success &&
                logsResult is Result.Success
            ) {
                _uiState.value = UiState.Success(
                    settingsResult.data, modelsResult.data, credentialsResult.data, logsResult.data,
                )
            } else {
                val message = listOf(settingsResult, modelsResult, credentialsResult, logsResult)
                    .filterIsInstance<Result.Error>()
                    .firstOrNull()?.message ?: "Ayarlar yuklenemedi."
                _uiState.value = UiState.Error(message)
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
    ) {
        viewModelScope.launch {
            val result = repository.updateEngineConfig(
                mode, localModelName, cloudProvider, cloudApiKey, temperature, maxTokens, topP,
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
}
