package org.structured_barebone.cmp_project

import org.structured_barebone.shared.model.AppLanguage

/**
 * Makes [language] the language of the process. Compose Resources read the platform default locale when a
 * composition is built, so callers must rebuild the UI after changing it (see [App]).
 */
expect fun applyAppLocale(language: AppLanguage)
