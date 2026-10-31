package org.structured_barebone.cmp_project

import org.structured_barebone.shared.model.AppLanguage
import java.util.Locale

/** The locale the process started with; used to follow the system language again. */
private val systemLocale: Locale = Locale.getDefault()

actual fun applyAppLocale(language: AppLanguage) {
    Locale.setDefault(language.tag?.let(Locale::forLanguageTag) ?: systemLocale)
}
