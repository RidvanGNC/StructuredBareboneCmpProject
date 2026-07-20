package org.structured_barebone.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module
import org.structured_barebone.data.auth.AuthService
import org.structured_barebone.data.auth.MsalAuthService

fun platformModule(context: Context) = module {
    single<AuthService> { MsalAuthService(context) }
    single<Settings> {
        SharedPreferencesSettings(context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE))
    }
}
