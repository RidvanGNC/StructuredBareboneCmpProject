package org.structured_barebone.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import org.structured_barebone.data.domain.SecureKeyStore
import org.structured_barebone.data.implementation.IosSecureKeyStore
import platform.Foundation.NSUserDefaults

// Unverified: iOS targets cannot be built on the development machine used for this template.
actual val platformModule = module {
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
    single<SecureKeyStore> { IosSecureKeyStore() }
}
