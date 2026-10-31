package org.structured_barebone.di

import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.structured_barebone.data.client.AppConfig
import org.structured_barebone.data.client.AppLog
import org.structured_barebone.data.client.AuthPaths
import org.structured_barebone.data.client.HttpClientOptions
import org.structured_barebone.data.client.RevokeSessionStep
import org.structured_barebone.data.client.SessionCleaner
import org.structured_barebone.data.client.SignOutStep
import org.structured_barebone.data.client.TokenProvider
import org.structured_barebone.data.client.TokenStorage
import org.structured_barebone.data.client.UpdateGate
import org.structured_barebone.data.client.createHttpClient
import org.structured_barebone.data.domain.AppearanceRepository
import org.structured_barebone.data.domain.AuthRepository
import org.structured_barebone.data.domain.LanguageRepository
import org.structured_barebone.data.domain.TemplateRepository
import org.structured_barebone.data.implementation.AppearanceRepositoryImpl
import org.structured_barebone.data.implementation.AuthRepositoryImpl
import org.structured_barebone.data.implementation.LanguageRepositoryImpl
import org.structured_barebone.data.implementation.TemplateRepositoryImpl
import org.structured_barebone.feature.AllFeaturesVisible
import org.structured_barebone.feature.FeatureModule
import org.structured_barebone.feature.FeatureRegistry
import org.structured_barebone.feature.FeatureVisibilityPolicy
import org.structured_barebone.shared.model.AppMessages
import org.structured_barebone.viewmodel.AppearanceViewModel
import org.structured_barebone.viewmodel.LanguageViewModel
import org.structured_barebone.viewmodel.LoginViewModel
import org.structured_barebone.viewmodel.ProfileViewModel
import org.structured_barebone.viewmodel.SessionViewModel
import org.structured_barebone.viewmodel.TemplateViewModel

/**
 * Starts Koin with the base module. [config] carries the deployment settings of the host and [features] the
 * feature modules compiled into the app; their Koin modules are loaded too. Modules the host adds in
 * [appDeclaration] are loaded last, so they can replace definitions, for example the [FeatureVisibilityPolicy].
 */
fun initKoin(
    config: AppConfig = AppConfig(),
    features: List<FeatureModule> = emptyList(),
    appDeclaration: KoinAppDeclaration = {},
) {
    startKoin {
        modules(commonModule(config, features))
        modules(features.flatMap { it.koinModules })
        appDeclaration()
    }
}

fun commonModule(config: AppConfig = AppConfig(), features: List<FeatureModule> = emptyList()) = module {
    includes(platformModule)
    single { config }
    single { UpdateGate() }
    single { AppMessages() }
    single { FeatureRegistry(features, getOrNull<FeatureVisibilityPolicy>() ?: AllFeaturesVisible) }
    single<TokenProvider> { TokenStorage(get(), get()) }
    single<HttpClient> {
        createHttpClient(
            HttpClientOptions(
                tokenProvider = get(),
                refreshUrl = if (config.authEnabled) config.url(AuthPaths.REFRESH) else null,
                updateGate = get(),
                enableNetworkLogging = AppLog.debug,
            ),
        )
    }
    factory<SignOutStep> { RevokeSessionStep(get(), get(), get()) }
    single { SessionCleaner(get(), getAll<SignOutStep>()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<TemplateRepository> { TemplateRepositoryImpl() }
    single<LanguageRepository> { LanguageRepositoryImpl(get()) }
    single<AppearanceRepository> { AppearanceRepositoryImpl(get()) }
    factory { TemplateViewModel(get()) }
    factory { LanguageViewModel(get()) }
    factory { AppearanceViewModel(get()) }
    factory { LoginViewModel(get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { SessionViewModel(get(), get(), get(), get(), get()) }
}
