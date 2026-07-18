package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.AccountRepository
import org.structured_barebone.shared.model.AccountStatusDto
import org.structured_barebone.shared.model.AuditLogEntryDto
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.TenantInvitationDto
import org.structured_barebone.shared.model.TenantPermission
import org.structured_barebone.shared.model.TenantUserDto

class AccountViewModel(
    private val repository: AccountRepository,
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    sealed class UiState {
        data object Loading : UiState()
        data class Success(
            val status: AccountStatusDto,
            val users: List<TenantUserDto>,
            val invitations: List<TenantInvitationDto>,
            val auditLog: List<AuditLogEntryDto>,
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
            val statusResult = repository.getStatus()
            val usersResult = repository.getTenantUsers()
            val invitationsResult = repository.getInvitations()
            val auditLogResult = repository.getAuditLog()

            if (statusResult is Result.Success &&
                usersResult is Result.Success &&
                invitationsResult is Result.Success &&
                auditLogResult is Result.Success
            ) {
                _uiState.value = UiState.Success(
                    statusResult.data, usersResult.data, invitationsResult.data, auditLogResult.data,
                )
            } else {
                val message = listOf(statusResult, usersResult, invitationsResult, auditLogResult)
                    .filterIsInstance<Result.Error>()
                    .firstOrNull()?.message ?: "Sirket bilgileri yuklenemedi."
                _uiState.value = UiState.Error(message)
            }
        }
    }

    fun createCompany(name: String) {
        viewModelScope.launch {
            val result = repository.createCompany(name)
            if (result is Result.Success) load()
        }
    }

    fun inviteUser(email: String, permissions: List<TenantPermission>) {
        viewModelScope.launch {
            val result = repository.inviteUser(email, permissions)
            if (result is Result.Success) load()
        }
    }

    fun revokeInvitation(id: String) {
        viewModelScope.launch {
            val result = repository.revokeInvitation(id)
            if (result is Result.Success) load()
        }
    }

    fun updateMemberPermissions(userId: String, permissions: List<TenantPermission>) {
        viewModelScope.launch {
            val result = repository.updateMemberPermissions(userId, permissions)
            if (result is Result.Success) load()
        }
    }
}
