package org.structured_barebone.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module
import org.structured_barebone.data.domain.SecureKeyStore
import org.structured_barebone.data.implementation.AndroidSecureKeyStore

actual val platformModule = module {
    single<Settings> { SharedPreferencesSettings(get<Context>().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)) }
    single<SecureKeyStore> { AndroidSecureKeyStore(get<Context>()) }
}

private const val PREFERENCES_NAME = "app_preferences"
