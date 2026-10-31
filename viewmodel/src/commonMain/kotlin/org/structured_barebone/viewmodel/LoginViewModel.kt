package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.AuthRepository
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.Result

/**
 * State of the sign-in form. [error] is data (kind and code); on success the session opens and the app shell
 * replaces the screen, so there is no "done" state.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailMissing: Boolean = false,
    val passwordMissing: Boolean = false,
    val submitting: Boolean = false,
    val error: AppError? = null,
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = mutableState.asStateFlow()

    fun onEmailChange(value: String) = mutableState.update { it.copy(email = value, emailMissing = false, error = null) }

    fun onPasswordChange(value: String) =
        mutableState.update { it.copy(password = value, passwordMissing = false, error = null) }

    fun submit() {
        val current = mutableState.value
        if (current.submitting) return
        val emailMissing = current.email.isBlank()
        val passwordMissing = current.password.isEmpty()
        if (emailMissing || passwordMissing) {
            mutableState.update { it.copy(emailMissing = emailMissing, passwordMissing = passwordMissing) }
            return
        }
        mutableState.update { it.copy(submitting = true, error = null) }
        viewModelScope.launch {
            when (val result = authRepository.login(current.email, current.password)) {
                is Result.Success -> mutableState.update { it.copy(submitting = false, password = "") }
                is Result.Error -> mutableState.update { it.copy(submitting = false, error = result.error) }
            }
        }
    }
}
