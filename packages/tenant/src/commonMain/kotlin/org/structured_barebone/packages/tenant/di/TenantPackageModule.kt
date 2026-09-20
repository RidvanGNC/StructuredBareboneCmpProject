package org.structured_barebone.packages.tenant.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.structured_barebone.data.client.createHttpClient
import org.structured_barebone.packages.tenant.data.KtorTenantRepository
import org.structured_barebone.packages.tenant.data.TenantRepository
import org.structured_barebone.packages.tenant.runtime.TenantSessionStore

data class TenantPackageConfig(val apiBaseUrl: String)

/** Add this module only in applications that opt into tenant-aware behavior. */
fun tenantPackageModule(config: TenantPackageConfig): Module = module {
    single<TenantRepository> { KtorTenantRepository(createHttpClient(), config.apiBaseUrl) }
    single { TenantSessionStore(get()) }
}
