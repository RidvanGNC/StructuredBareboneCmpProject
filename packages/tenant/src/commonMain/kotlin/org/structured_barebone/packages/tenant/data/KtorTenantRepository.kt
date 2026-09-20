package org.structured_barebone.packages.tenant.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import org.structured_barebone.packages.tenant.model.TenantBootstrap

class KtorTenantRepository(
    private val httpClient: HttpClient,
    apiBaseUrl: String,
) : TenantRepository {
    private val apiBaseUrl = apiBaseUrl.trimEnd('/')

    override suspend fun loadBootstrap(): TenantBootstrap =
        httpClient.get(endpoint("/v1/client/tenant-bootstrap")).body()

    override suspend fun selectActiveTenant(tenantId: String): TenantBootstrap =
        httpClient.put(endpoint("/v1/client/tenant-context")) {
            contentType(ContentType.Application.Json)
            setBody(SelectActiveTenantRequest(tenantId))
        }.body()

    private fun endpoint(path: String): String = "$apiBaseUrl$path"
}

@Serializable
private data class SelectActiveTenantRequest(val tenantId: String)
