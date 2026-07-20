package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.PricingRateRepository
import org.structured_barebone.shared.model.Currency
import org.structured_barebone.shared.model.PricingRateDto
import org.structured_barebone.shared.model.Result

class PricingRatesViewModel(
    private val repository: PricingRateRepository,
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val rates: List<PricingRateDto>) : UiState()
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

    fun upsert(languageCode: String, ratePerCharacter: Double, currency: Currency) {
        viewModelScope.launch {
            val result = repository.upsert(languageCode, ratePerCharacter, currency)
            if (result is Result.Success) load()
        }
    }

    fun remove(id: String) {
        viewModelScope.launch {
            val result = repository.remove(id)
            if (result is Result.Success) load()
        }
    }
}
