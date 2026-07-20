package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.SettingsRepository
import org.structured_barebone.data.domain.UserRepository
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.UserProfile
import org.structured_barebone.shared.model.VacationModeSettingsDto

class UserViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val profile: UserProfile) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _deletionRequested = MutableStateFlow(false)
    val deletionRequested: StateFlow<Boolean> = _deletionRequested.asStateFlow()

    private val _vacationMode = MutableStateFlow<VacationModeSettingsDto?>(null)
    val vacationMode: StateFlow<VacationModeSettingsDto?> = _vacationMode.asStateFlow()

    init {
        load()
        loadVacationMode()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = userRepository.getCurrentUser()) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun loadVacationMode() {
        viewModelScope.launch {
            val result = settingsRepository.getVacationMode()
            if (result is Result.Success) _vacationMode.value = result.data
        }
    }

    fun setVacationModeEnabled(isEnabled: Boolean) {
        val current = _vacationMode.value ?: return
        viewModelScope.launch {
            val result = settingsRepository.updateVacationMode(isEnabled, current.messageTemplate, current.notifyMailTypes)
            if (result is Result.Success) _vacationMode.value = result.data
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            when (userRepository.requestAccountDeletion()) {
                is Result.Success -> _deletionRequested.value = true
                is Result.Error -> Unit
            }
        }
    }
}
