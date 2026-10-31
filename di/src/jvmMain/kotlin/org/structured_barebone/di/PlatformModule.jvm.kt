package org.structured_barebone.di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val platformModule = module {
    single<Settings> { PreferencesSettings(Preferences.userRoot().node(PREFERENCES_NODE)) }
}

/** Rename together with the application id when the template is turned into a product. */
private const val PREFERENCES_NODE = "org/structured_barebone/app"
