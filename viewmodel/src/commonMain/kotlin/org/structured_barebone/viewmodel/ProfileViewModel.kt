package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.client.AppConfig
import org.structured_barebone.data.domain.AuthRepository
import org.structured_barebone.data.model.UserProfile
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.Result

class ProfileViewModel(
    private val config: AppConfig,
    private val authRepository: AuthRepository,
) : ViewModel() {
    sealed interface UiState {
        data object Loading : UiState

        /** The app runs without a backend, so there is no account to show. */
        data object NoAccount : UiState
        data class Success(val profile: UserProfile) : UiState
        data class Error(val error: AppError) : UiState
    }

    private val mutableState = MutableStateFlow<UiState>(if (config.authEnabled) UiState.Loading else UiState.NoAccount)
    val state: StateFlow<UiState> = mutableState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        if (!config.authEnabled) return
        mutableState.value = UiState.Loading
        viewModelScope.launch {
            mutableState.value = when (val result = authRepository.profile()) {
                is Result.Success -> UiState.Success(result.data)
                is Result.Error -> UiState.Error(result.error)
            }
        }
    }
}
