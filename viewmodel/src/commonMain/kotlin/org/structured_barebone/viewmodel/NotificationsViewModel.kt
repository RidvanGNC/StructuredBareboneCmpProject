package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.SettingsRepository
import org.structured_barebone.data.settings.AppPreferences
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.SystemNotificationDto

private const val REFRESH_INTERVAL_MS = 30_000L

class NotificationsViewModel(
    private val settingsRepository: SettingsRepository,
    private val appPreferences: AppPreferences,
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    private val _notifications = MutableStateFlow<List<SystemNotificationDto>>(emptyList())
    val notifications: StateFlow<List<SystemNotificationDto>> = _notifications.asStateFlow()

    val readIds: StateFlow<Set<String>> = appPreferences.readNotificationIds

    init {
        viewModelScope.launch {
            while (true) {
                refresh()
                delay(REFRESH_INTERVAL_MS)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val result = settingsRepository.getSystemNotifications()
            if (result is Result.Success) _notifications.value = result.data
        }
    }

    fun markAsRead(id: String) {
        appPreferences.markNotificationRead(id)
    }

    fun markAllAsRead() {
        appPreferences.markNotificationsRead(_notifications.value.map { it.id })
    }
}
