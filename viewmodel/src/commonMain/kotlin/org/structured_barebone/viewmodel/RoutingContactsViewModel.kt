package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.RoutingContactRepository
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.RoutingContactDto

class RoutingContactsViewModel(
    private val repository: RoutingContactRepository,
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val contacts: List<RoutingContactDto>) : UiState()
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
            when (val result = repository.getActive()) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun addContact(
        name: String,
        surname: String,
        email: String,
        languagePreferences: List<String>,
        isAvailable: Boolean,
        priority: Int,
    ) {
        viewModelScope.launch {
            val result = repository.add(name, surname, email, languagePreferences, isAvailable, priority)
            if (result is Result.Success) load()
        }
    }

    fun updateContact(
        id: String,
        name: String,
        surname: String,
        email: String,
        languagePreferences: List<String>,
        isAvailable: Boolean,
        priority: Int,
    ) {
        viewModelScope.launch {
            val result = repository.update(
                id,
                name,
                surname,
                email,
                languagePreferences,
                isAvailable,
                priority,
            )
            if (result is Result.Success) load()
        }
    }

    fun setAvailability(id: String, isAvailable: Boolean) {
        viewModelScope.launch {
            val result = repository.setAvailability(id, isAvailable)
            if (result is Result.Success) load()
        }
    }

    fun removeContact(id: String) {
        viewModelScope.launch {
            val result = repository.remove(id)
            if (result is Result.Success) load()
        }
    }
}
