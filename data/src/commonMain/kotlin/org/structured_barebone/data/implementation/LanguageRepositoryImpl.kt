package org.structured_barebone.data.implementation

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.structured_barebone.data.domain.LanguageRepository
import org.structured_barebone.shared.model.AppLanguage

class LanguageRepositoryImpl(private val settings: Settings) : LanguageRepository {
    private val mutableLanguage = MutableStateFlow(AppLanguage.fromStorage(settings.getStringOrNull(KEY_LANGUAGE)))
    override val language: StateFlow<AppLanguage> = mutableLanguage.asStateFlow()

    override fun select(language: AppLanguage) {
        settings.putString(KEY_LANGUAGE, language.storageValue)
        mutableLanguage.value = language
    }

    companion object {
        const val KEY_LANGUAGE = "language"
    }
}
