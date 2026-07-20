package org.structured_barebone.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import org.structured_barebone.data.auth.AuthService
import org.structured_barebone.data.auth.MsalAuthService
import platform.Foundation.NSUserDefaults

fun platformModule() = module {
    single<AuthService> { MsalAuthService() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
}
