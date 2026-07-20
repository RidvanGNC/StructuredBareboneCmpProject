package org.structured_barebone.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module
import org.structured_barebone.data.auth.AuthService
import org.structured_barebone.data.auth.MsalAuthService
import org.structured_barebone.data.platform.AndroidAttachmentSaver
import org.structured_barebone.data.platform.AttachmentSaver

fun platformModule(context: Context) = module {
    single<AuthService> { MsalAuthService(context) }
    single<Settings> {
        SharedPreferencesSettings(context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE))
    }
    single<AttachmentSaver> { AndroidAttachmentSaver(context) }
}
