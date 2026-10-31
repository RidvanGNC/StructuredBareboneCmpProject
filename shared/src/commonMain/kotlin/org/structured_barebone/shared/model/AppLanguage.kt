package org.structured_barebone.shared.model

/**
 * Languages the app can show. To add one, add an entry here and a `values-<tag>/strings.xml` file next to
 * every `values/strings.xml`; the resource parity test fails until all files have the same keys.
 *
 * @property tag BCP 47 language tag, or null to follow the system language.
 * @property storageValue stable value written to the preferences; never rename it.
 */
enum class AppLanguage(val tag: String?, val storageValue: String) {
    SYSTEM(null, "system"),
    ENGLISH("en", "en"),
    TURKISH("tr", "tr"),
    ;

    companion object {
        fun fromStorage(value: String?): AppLanguage = entries.firstOrNull { it.storageValue == value } ?: SYSTEM
    }
}
