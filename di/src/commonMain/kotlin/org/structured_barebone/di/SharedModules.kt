package org.structured_barebone.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.structured_barebone.data.client.ApiClient
import org.structured_barebone.data.domain.TemplateRepository
import org.structured_barebone.data.implementation.TemplateRepositoryImpl
import org.structured_barebone.viewmodel.TemplateViewModel

fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(commonModule())
}

fun commonModule() = module {
    single { ApiClient.httpClient }

    single<TemplateRepository> { TemplateRepositoryImpl(get()) }
    factory { TemplateViewModel(get()) }
}