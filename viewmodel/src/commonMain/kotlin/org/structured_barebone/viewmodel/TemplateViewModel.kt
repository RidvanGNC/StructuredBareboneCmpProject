package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.TemplateRepository
import org.structured_barebone.shared.model.Result

class TemplateViewModel(
    private val templateRepository: TemplateRepository
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    sealed class UiState {
        data object Idle : UiState()
        data object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _templateValue = MutableStateFlow<String>("")
    val templateValue: StateFlow<String> = _templateValue.asStateFlow()

    init {
        templateViewModelFunction()
    }

    fun templateViewModelFunction() {
        viewModelScope.launch {
            when (val result = templateRepository.template()) {
                is Result.Success -> _templateValue.value = result.data
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }
}