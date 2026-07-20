package org.structured_barebone.cmp_project

import java.util.Locale

actual fun systemLanguageTag(): String = Locale.getDefault().language

actual fun applyLanguageTag(tag: String) {
    Locale.setDefault(Locale.forLanguageTag(tag))
}
