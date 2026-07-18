package org.structured_barebone.di

import org.koin.dsl.module
import org.structured_barebone.data.auth.AuthService
import org.structured_barebone.data.auth.UnimplementedAuthService

fun platformModule() = module {
    single<AuthService> { UnimplementedAuthService() }
}
