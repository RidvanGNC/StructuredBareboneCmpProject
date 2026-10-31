package org.structured_barebone.shared.model

/** How the app chooses between its light and dark color scheme. */
enum class ThemeMode(val storageValue: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromStorage(value: String?): ThemeMode = entries.firstOrNull { it.storageValue == value } ?: SYSTEM
    }
}
