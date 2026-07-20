package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.ManagedMailGroupRepository
import org.structured_barebone.shared.model.ManagedMailGroupDto
import org.structured_barebone.shared.model.Result

class MailGroupsViewModel(
    private val repository: ManagedMailGroupRepository,
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val groups: List<ManagedMailGroupDto>) : UiState()
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
            when (val result = repository.getAll()) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun add(mailGroup: String) {
        viewModelScope.launch {
            val result = repository.add(mailGroup)
            if (result is Result.Success) load()
        }
    }

    fun setActive(id: String, isActive: Boolean) {
        viewModelScope.launch {
            val result = repository.setActive(id, isActive)
            if (result is Result.Success) load()
        }
    }

    fun setNote(id: String, note: String?) {
        viewModelScope.launch {
            val result = repository.setNote(id, note)
            if (result is Result.Success) load()
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            val result = repository.delete(id)
            if (result is Result.Success) load()
        }
    }
}
