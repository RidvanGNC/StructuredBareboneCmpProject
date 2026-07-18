package org.structured_barebone.data.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.structured_barebone.data.client.ApiConfig
import org.structured_barebone.data.domain.MailFilter
import org.structured_barebone.data.domain.MailRepository
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.SyncResultDto
import org.structured_barebone.shared.model.CustomerDto
import org.structured_barebone.shared.model.MailDto
import org.structured_barebone.shared.model.Result

class MailRepositoryImpl(
    private val httpClient: HttpClient,
) : BaseRepository(), MailRepository {

    override suspend fun getMails(filter: MailFilter): Result<List<MailDto>> =
        safeRequest {
            val mails = if (ApiConfig.USE_MOCK) {
                MockMailData.mails.filter { mail ->
                    (filter.status == null || mail.status == filter.status) &&
                        (filter.type == null || mail.type == filter.type) &&
                        (filter.hasAttachment == null || mail.hasAttachment == filter.hasAttachment) &&
                        (filter.isUrgent == null || mail.isUrgent == filter.isUrgent) &&
                        (filter.mailGroup == null || mail.mailGroup == filter.mailGroup)
                }
            } else {
                httpClient.get("/mails") {
                    parameter("status", filter.status)
                    parameter("type", filter.type)
                    parameter("hasAttachment", filter.hasAttachment)
                    parameter("isUrgent", filter.isUrgent)
                    parameter("mailGroup", filter.mailGroup)
                }.body()
            }
            ApiResponse(success = true, message = null, data = mails)
        }

    override suspend fun getMailById(id: String): Result<MailDto> =
        safeRequest {
            val mail = if (ApiConfig.USE_MOCK) {
                MockMailData.mails.find { it.id == id }
            } else {
                // Backend'de tekil mail endpoint'i yok; web istemcisiyle ayni sekilde listeden buluyoruz.
                httpClient.get("/mails").body<List<MailDto>>().find { it.id == id }
            }
            requireNotNull(mail) { "Mail bulunamadi" }
            ApiResponse(success = true, message = null, data = mail)
        }

    override suspend fun getCustomers(): Result<List<CustomerDto>> =
        safeRequest {
            val customers = if (ApiConfig.USE_MOCK) {
                MockMailData.customers
            } else {
                httpClient.get("/customers").body()
            }
            ApiResponse(success = true, message = null, data = customers)
        }

    override suspend fun getMailGroups(): Result<List<String>> =
        safeRequest {
            val groups = if (ApiConfig.USE_MOCK) {
                MockMailData.mails.map { it.mailGroup }.distinct().sorted()
            } else {
                httpClient.get("/mails/groups").body()
            }
            ApiResponse(success = true, message = null, data = groups)
        }

    override suspend fun syncMails(): Result<Int> =
        safeRequest {
            val ingested = if (ApiConfig.USE_MOCK) {
                0
            } else {
                httpClient.post("/mails/sync").body<SyncResultDto>().ingested
            }
            ApiResponse(success = true, message = null, data = ingested)
        }
}
