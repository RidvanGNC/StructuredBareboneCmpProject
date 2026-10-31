package org.structured_barebone.shared.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MessageSeverity { INFO, WARNING, ERROR }

/**
 * A message for the user, described by data only: the UI maps [error] to localized text, so a language change
 * never leaves stale text behind.
 */
data class AppMessage(val severity: MessageSeverity, val error: AppError)

/** The one message strip of the app. Posting replaces the current message; the shell shows and dismisses it. */
class AppMessages {
    private val mutableCurrent = MutableStateFlow<AppMessage?>(null)
    val current: StateFlow<AppMessage?> = mutableCurrent.asStateFlow()

    fun post(message: AppMessage) {
        mutableCurrent.value = message
    }

    fun dismiss() {
        mutableCurrent.value = null
    }
}
