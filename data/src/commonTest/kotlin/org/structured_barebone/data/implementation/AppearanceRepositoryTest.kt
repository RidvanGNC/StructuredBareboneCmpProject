package org.structured_barebone.data.implementation

import com.russhwolf.settings.MapSettings
import org.structured_barebone.shared.model.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals

class AppearanceRepositoryTest {
    @Test
    fun follows_the_system_until_a_mode_is_picked() {
        assertEquals(ThemeMode.SYSTEM, AppearanceRepositoryImpl(MapSettings()).themeMode.value)
    }

    @Test
    fun picked_mode_is_saved_and_restored() {
        val settings = MapSettings()
        AppearanceRepositoryImpl(settings).select(ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, AppearanceRepositoryImpl(settings).themeMode.value)
    }

    @Test
    fun unknown_stored_value_falls_back_to_the_system() {
        val settings = MapSettings(AppearanceRepositoryImpl.KEY_THEME_MODE to "sepia")

        assertEquals(ThemeMode.SYSTEM, AppearanceRepositoryImpl(settings).themeMode.value)
    }
}
