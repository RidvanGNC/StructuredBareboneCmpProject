package org.structured_barebone.di

import android.content.Context
import org.koin.dsl.module
import org.structured_barebone.data.auth.AuthService
import org.structured_barebone.data.auth.MsalAuthService

fun platformModule(context: Context) = module {
    single<AuthService> { MsalAuthService(context) }
}
