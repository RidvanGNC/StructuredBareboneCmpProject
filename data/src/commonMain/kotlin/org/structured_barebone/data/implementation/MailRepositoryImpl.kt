package org.structured_barebone.data.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.structured_barebone.data.client.ApiConfig
import org.structured_barebone.data.domain.MailFilter
import org.structured_barebone.data.domain.MailRepository
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.ConfirmLanguageRequest
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
                        (filter.predictedLanguage == null || mail.predictedLanguage == filter.predictedLanguage) &&
                        (filter.confirmedLanguage == null || mail.confirmedLanguage == filter.confirmedLanguage) &&
                        (filter.type == null || mail.type == filter.type) &&
                        (filter.hasAttachment == null || mail.hasAttachment == filter.hasAttachment) &&
                        (filter.isUrgent == null || mail.isUrgent == filter.isUrgent) &&
                        (filter.mailGroup == null || mail.mailGroup == filter.mailGroup) &&
                        (filter.isNotarized == null || mail.isNotarized == filter.isNotarized)
                }
            } else {
                httpClient.get("/mails") {
                    parameter("status", filter.status)
                    parameter("predictedLanguage", filter.predictedLanguage)
                    parameter("confirmedLanguage", filter.confirmedLanguage)
                    parameter("type", filter.type)
                    parameter("hasAttachment", filter.hasAttachment)
                    parameter("isUrgent", filter.isUrgent)
                    parameter("mailGroup", filter.mailGroup)
                    parameter("replyStatus", filter.replyStatus)
                    parameter("isNotarized", filter.isNotarized)
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

    override suspend fun confirmLanguage(id: String, language: String): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                val index = MockMailData.mails.indexOfFirst { it.id == id }
                if (index >= 0) {
                    MockMailData.mails[index] = MockMailData.mails[index].copy(confirmedLanguage = language)
                }
            } else {
                httpClient.post("/mails/$id/confirm-language") {
                    contentType(ContentType.Application.Json)
                    setBody(ConfirmLanguageRequest(language))
                }
            }
            ApiResponse(success = true, message = null, data = Unit)
        }

    override suspend fun reanalyze(id: String): Result<MailDto> =
        safeRequest {
            val mail = if (ApiConfig.USE_MOCK) {
                requireNotNull(MockMailData.mails.find { it.id == id }) { "Mail bulunamadi" }
            } else {
                httpClient.post("/mails/$id/reanalyze").body()
            }
            ApiResponse(success = true, message = null, data = mail)
        }

    override suspend fun downloadAttachment(id: String): Result<ByteArray> =
        safeRequest {
            val bytes = if (ApiConfig.USE_MOCK) {
                ByteArray(0)
            } else {
                httpClient.get("/mails/$id/attachment").body<ByteArray>()
            }
            ApiResponse(success = true, message = null, data = bytes)
        }
}
