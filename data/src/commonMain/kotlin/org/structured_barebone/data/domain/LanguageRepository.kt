package org.structured_barebone.data.domain

import kotlinx.coroutines.flow.StateFlow
import org.structured_barebone.shared.model.AppLanguage

/** The language the user picked; persisted on the device. */
interface LanguageRepository {
    val language: StateFlow<AppLanguage>

    fun select(language: AppLanguage)
}
