package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.structured_barebone.data.domain.LanguageRepository
import org.structured_barebone.shared.model.AppLanguage

class LanguageViewModel(private val languageRepository: LanguageRepository) : ViewModel() {
    val language: StateFlow<AppLanguage> = languageRepository.language

    fun select(language: AppLanguage) = languageRepository.select(language)
}
