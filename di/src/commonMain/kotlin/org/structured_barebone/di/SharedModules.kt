package org.structured_barebone.di

import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.structured_barebone.data.auth.AuthService
import org.structured_barebone.data.client.createHttpClient
import org.structured_barebone.data.client.platformBaseUrl
import org.structured_barebone.data.domain.MailReplyRepository
import org.structured_barebone.data.domain.MailRepository
import org.structured_barebone.data.domain.ManagedMailGroupRepository
import org.structured_barebone.data.domain.PricingRateRepository
import org.structured_barebone.data.domain.RoutingContactRepository
import org.structured_barebone.data.domain.SettingsRepository
import org.structured_barebone.data.domain.UserRepository
import org.structured_barebone.data.implementation.MailReplyRepositoryImpl
import org.structured_barebone.data.implementation.MailRepositoryImpl
import org.structured_barebone.data.implementation.ManagedMailGroupRepositoryImpl
import org.structured_barebone.data.implementation.PricingRateRepositoryImpl
import org.structured_barebone.data.implementation.RoutingContactRepositoryImpl
import org.structured_barebone.data.implementation.SettingsRepositoryImpl
import org.structured_barebone.data.implementation.UserRepositoryImpl
import org.structured_barebone.data.settings.AppPreferences
import org.structured_barebone.viewmodel.MailDetailViewModel
import org.structured_barebone.viewmodel.MailGroupsViewModel
import org.structured_barebone.viewmodel.MailListViewModel
import org.structured_barebone.viewmodel.NotificationsViewModel
import org.structured_barebone.viewmodel.PricingRatesViewModel
import org.structured_barebone.viewmodel.RoutingContactsViewModel
import org.structured_barebone.viewmodel.SettingsViewModel
import org.structured_barebone.viewmodel.UserViewModel

fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(commonModule())
}

fun commonModule() = module {
    single { AppPreferences(get()) }

    single<HttpClient> {
        val appPreferences = get<AppPreferences>()
        createHttpClient(
            tokenProvider = { get<AuthService>().getAccessToken() },
            graphTokenProvider = { get<AuthService>().getGraphAccessToken() },
            baseUrlProvider = { appPreferences.apiBaseUrlOverride.value ?: platformBaseUrl },
        )
    }

    single<MailRepository> { MailRepositoryImpl(get()) }
    factory { MailListViewModel(get()) }

    single<ManagedMailGroupRepository> { ManagedMailGroupRepositoryImpl(get()) }
    factory { MailGroupsViewModel(get()) }

    single<UserRepository> { UserRepositoryImpl(get()) }
    factory { UserViewModel(get(), get()) }

    single<MailReplyRepository> { MailReplyRepositoryImpl(get()) }
    factory { (mailId: String) -> MailDetailViewModel(mailId, get(), get(), get()) }

    single<RoutingContactRepository> { RoutingContactRepositoryImpl(get()) }
    factory { RoutingContactsViewModel(get()) }

    single<PricingRateRepository> { PricingRateRepositoryImpl(get()) }
    factory { PricingRatesViewModel(get()) }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    factory { SettingsViewModel(get()) }
    factory { NotificationsViewModel(get(), get()) }
}
