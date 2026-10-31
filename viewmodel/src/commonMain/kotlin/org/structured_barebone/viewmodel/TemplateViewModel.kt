package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.TemplateRepository
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.Result

class TemplateViewModel(
    private val templateRepository: TemplateRepository
) : ViewModel() {
    sealed interface UiState {
        data object Loading : UiState
        data class Success(val value: String) : UiState
        data class Error(val error: AppError) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = templateRepository.template()) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.error)
            }
        }
    }
}
