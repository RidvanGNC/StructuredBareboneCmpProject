package org.structured_barebone.data.settings

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val KEY_LANGUAGE_TAG = "app_language_tag"
private const val KEY_API_BASE_URL_OVERRIDE = "api_base_url_override"

// Web'deki SUPPORTED_UI_LANGUAGES ile ayni 5 dil (tr/en/ar/ru/de).
val SUPPORTED_LANGUAGE_TAGS = listOf("tr", "en", "ar", "ru", "de")
const val DEFAULT_LANGUAGE_TAG = "tr"

/**
 * Kalici (SharedPreferences/NSUserDefaults tabanli) kullanici tercihleri.
 * Dil tercihi ve sunucu adresi override'i icin gozlemlenebilir (StateFlow) bir katman saglar;
 * `multiplatform-settings`'in cekirdek `Settings` API'si senkron oldugu icin degerler
 * baslangicta okunup bir `MutableStateFlow` icine yuklenir, degisiklikler hem kalici depoya
 * hem de akisa ayni anda yazilir.
 */
class AppPreferences(private val settings: Settings) {
    // null = kullanici hicbir dil secmedi, sistem dili kullanilir.
    private val _languageTag = MutableStateFlow(settings.getStringOrNull(KEY_LANGUAGE_TAG))
    val languageTag: StateFlow<String?> = _languageTag

    fun setLanguageTag(tag: String?) {
        if (tag == null) {
            settings.remove(KEY_LANGUAGE_TAG)
        } else {
            settings.putString(KEY_LANGUAGE_TAG, tag)
        }
        _languageTag.value = tag
    }

    // null = override yok, platformun varsayilan adresi kullanilir.
    private val _apiBaseUrlOverride = MutableStateFlow(settings.getStringOrNull(KEY_API_BASE_URL_OVERRIDE))
    val apiBaseUrlOverride: StateFlow<String?> = _apiBaseUrlOverride

    fun setApiBaseUrlOverride(url: String?) {
        val trimmed = url?.trim()?.takeIf { it.isNotEmpty() }
        if (trimmed == null) {
            settings.remove(KEY_API_BASE_URL_OVERRIDE)
        } else {
            settings.putString(KEY_API_BASE_URL_OVERRIDE, trimmed)
        }
        _apiBaseUrlOverride.value = trimmed
    }
}
