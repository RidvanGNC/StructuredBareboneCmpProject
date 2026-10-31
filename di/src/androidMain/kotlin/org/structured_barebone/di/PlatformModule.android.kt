package org.structured_barebone.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module

actual val platformModule = module {
    single<Settings> { SharedPreferencesSettings(get<Context>().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)) }
}

private const val PREFERENCES_NAME = "app_preferences"
