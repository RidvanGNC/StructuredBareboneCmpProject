package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.MailFilter
import org.structured_barebone.data.domain.MailRepository
import org.structured_barebone.shared.model.CustomerDto
import org.structured_barebone.shared.model.MailDto
import org.structured_barebone.shared.model.Result

class MailListViewModel(
    private val mailRepository: MailRepository,
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    sealed class UiState {
        data object Loading : UiState()
        data class Success(
            val mails: List<MailDto>,
            val customers: List<CustomerDto>,
            val mailGroups: List<String>,
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow(MailFilter())
    val filter: StateFlow<MailFilter> = _filter.asStateFlow()

    init {
        load()
    }

    fun updateFilter(filter: MailFilter) {
        _filter.value = filter
        load()
    }

    fun sync() {
        viewModelScope.launch {
            val result = mailRepository.syncMails()
            if (result is Result.Success) load()
        }
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val mailsResult = mailRepository.getMails(_filter.value)
            val customersResult = mailRepository.getCustomers()
            val mailGroupsResult = mailRepository.getMailGroups()

            when {
                mailsResult is Result.Success && customersResult is Result.Success && mailGroupsResult is Result.Success ->
                    _uiState.value = UiState.Success(mailsResult.data, customersResult.data, mailGroupsResult.data)
                mailsResult is Result.Error -> _uiState.value = UiState.Error(mailsResult.message)
                customersResult is Result.Error -> _uiState.value = UiState.Error(customersResult.message)
                mailGroupsResult is Result.Error -> _uiState.value = UiState.Error(mailGroupsResult.message)
            }
        }
    }
}
