package org.structured_barebone.cmp_project

import org.structured_barebone.shared.model.AppLanguage
import platform.Foundation.NSUserDefaults

// Unverified: iOS targets cannot be built on the development machine used for this template.
// iOS reads the app language when the process starts, so a change takes effect after a restart.
actual fun applyAppLocale(language: AppLanguage) {
    val defaults = NSUserDefaults.standardUserDefaults
    val tag = language.tag
    if (tag == null) defaults.removeObjectForKey(APPLE_LANGUAGES_KEY) else defaults.setObject(listOf(tag), APPLE_LANGUAGES_KEY)
}

private const val APPLE_LANGUAGES_KEY = "AppleLanguages"
