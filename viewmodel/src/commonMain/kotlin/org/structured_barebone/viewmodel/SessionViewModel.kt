package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.structured_barebone.data.client.AppConfig
import org.structured_barebone.data.client.SessionCleaner
import org.structured_barebone.data.client.TokenProvider
import org.structured_barebone.data.domain.AuthRepository
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.AppMessage
import org.structured_barebone.shared.model.AppMessages
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.shared.model.MessageSeverity
import org.structured_barebone.shared.model.Result

/** Where the app is in the sign-in lifecycle; the app shell shows the matching content. */
enum class SessionState { CHECKING, SIGNED_OUT, SIGNED_IN }

/**
 * Owns the session gate. Without a configured backend there is no sign-in and the state is always
 * [SessionState.SIGNED_IN]. With one, a stored session is checked against the server once at start; a network
 * failure keeps the user signed in (offline use), a rejected session signs them out. A session that ends
 * without the user asking (expired refresh token) posts a message for the sign-in screen.
 */
class SessionViewModel(
    private val config: AppConfig,
    private val tokens: TokenProvider,
    private val authRepository: AuthRepository,
    private val sessionCleaner: SessionCleaner,
    private val messages: AppMessages,
) : ViewModel() {
    private val validated = MutableStateFlow(!config.authEnabled || !tokens.isLoggedIn.value)
    private var signOutRequestedByUser = false

    val state: StateFlow<SessionState> = combine(tokens.isLoggedIn, validated) { loggedIn, checked ->
        when {
            !config.authEnabled -> SessionState.SIGNED_IN
            !loggedIn -> SessionState.SIGNED_OUT
            !checked -> SessionState.CHECKING
            else -> SessionState.SIGNED_IN
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialState())

    init {
        if (config.authEnabled) {
            viewModelScope.launch { watchForExpiredSession() }
            if (tokens.isLoggedIn.value) viewModelScope.launch { validateStoredSession() }
        }
    }

    fun signOut() {
        signOutRequestedByUser = true
        viewModelScope.launch { sessionCleaner.signOut() }
    }

    private fun initialState(): SessionState = when {
        !config.authEnabled -> SessionState.SIGNED_IN
        !tokens.isLoggedIn.value -> SessionState.SIGNED_OUT
        else -> SessionState.CHECKING
    }

    private suspend fun validateStoredSession() {
        val result = authRepository.profile()
        if (result is Result.Error && result.error.kind == ErrorKind.UNAUTHORIZED) tokens.clear()
        validated.value = true
    }

    private suspend fun watchForExpiredSession() {
        var wasLoggedIn = tokens.isLoggedIn.value
        tokens.isLoggedIn.collect { loggedIn ->
            if (wasLoggedIn && !loggedIn) {
                // The flag outlives the sign-out call on purpose: this collector may run after it returned.
                if (!signOutRequestedByUser) {
                    messages.post(AppMessage(MessageSeverity.INFO, AppError(ErrorKind.UNAUTHORIZED)))
                }
                signOutRequestedByUser = false
            }
            if (loggedIn) signOutRequestedByUser = false
            wasLoggedIn = loggedIn
        }
    }
}
