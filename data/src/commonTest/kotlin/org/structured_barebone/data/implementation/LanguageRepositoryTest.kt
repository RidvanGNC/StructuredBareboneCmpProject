package org.structured_barebone.data.implementation

import com.russhwolf.settings.MapSettings
import org.structured_barebone.shared.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageRepositoryTest {
    @Test
    fun follows_the_system_language_until_one_is_picked() {
        assertEquals(AppLanguage.SYSTEM, LanguageRepositoryImpl(MapSettings()).language.value)
    }

    @Test
    fun picked_language_is_saved_and_restored() {
        val settings = MapSettings()
        LanguageRepositoryImpl(settings).select(AppLanguage.TURKISH)

        assertEquals(AppLanguage.TURKISH, LanguageRepositoryImpl(settings).language.value)
    }

    @Test
    fun selection_updates_the_flow() {
        val repository = LanguageRepositoryImpl(MapSettings())
        repository.select(AppLanguage.ENGLISH)

        assertEquals(AppLanguage.ENGLISH, repository.language.value)
    }

    @Test
    fun unknown_stored_value_falls_back_to_the_system_language() {
        val settings = MapSettings(LanguageRepositoryImpl.KEY_LANGUAGE to "xx")

        assertEquals(AppLanguage.SYSTEM, LanguageRepositoryImpl(settings).language.value)
    }
}
