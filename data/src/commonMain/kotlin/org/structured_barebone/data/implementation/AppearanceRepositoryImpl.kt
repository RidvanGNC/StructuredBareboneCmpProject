package org.structured_barebone.data.implementation

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.structured_barebone.data.domain.AppearanceRepository
import org.structured_barebone.shared.model.ThemeMode

class AppearanceRepositoryImpl(private val settings: Settings) : AppearanceRepository {
    private val mutableMode = MutableStateFlow(ThemeMode.fromStorage(settings.getStringOrNull(KEY_THEME_MODE)))
    override val themeMode: StateFlow<ThemeMode> = mutableMode.asStateFlow()

    override fun select(mode: ThemeMode) {
        settings.putString(KEY_THEME_MODE, mode.storageValue)
        mutableMode.value = mode
    }

    companion object {
        const val KEY_THEME_MODE = "theme_mode"
    }
}
