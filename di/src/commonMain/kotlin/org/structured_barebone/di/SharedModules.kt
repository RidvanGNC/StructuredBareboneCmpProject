package org.structured_barebone.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.structured_barebone.data.client.UpdateGate
import org.structured_barebone.data.domain.LanguageRepository
import org.structured_barebone.data.domain.TemplateRepository
import org.structured_barebone.data.implementation.LanguageRepositoryImpl
import org.structured_barebone.data.implementation.TemplateRepositoryImpl
import org.structured_barebone.viewmodel.LanguageViewModel
import org.structured_barebone.viewmodel.TemplateViewModel

fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
) {
    startKoin {
        appDeclaration()
        modules(commonModule())
    }
}

fun commonModule() = module {
    includes(platformModule)
    single { UpdateGate() }
    single<TemplateRepository> { TemplateRepositoryImpl() }
    single<LanguageRepository> { LanguageRepositoryImpl(get()) }
    factory { TemplateViewModel(get()) }
    factory { LanguageViewModel(get()) }
}
