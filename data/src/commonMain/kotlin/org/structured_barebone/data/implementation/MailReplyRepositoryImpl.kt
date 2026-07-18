package org.structured_barebone.data.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.structured_barebone.data.client.ApiConfig
import org.structured_barebone.data.domain.MailReplyRepository
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.ApproveReplyRequest
import org.structured_barebone.data.model.UpdateReplyRequest
import org.structured_barebone.shared.model.MailReplyDto
import org.structured_barebone.shared.model.MailReplyType
import org.structured_barebone.shared.model.Result

class MailReplyRepositoryImpl(
    private val httpClient: HttpClient,
) : BaseRepository(), MailReplyRepository {

    override suspend fun getReplies(mailId: String): Result<List<MailReplyDto>> =
        safeRequest {
            val replies = if (ApiConfig.USE_MOCK) {
                MockMailData.mailReplies.filter { it.mailId == mailId }
            } else {
                httpClient.get("/mails/$mailId/replies").body()
            }
            ApiResponse(success = true, message = null, data = replies)
        }

    override suspend fun generateQuoteDraft(mailId: String): Result<MailReplyDto> =
        generateDraft(mailId, MailReplyType.Quote)

    override suspend fun generateDeliveryDraft(mailId: String): Result<MailReplyDto> =
        generateDraft(mailId, MailReplyType.Delivery)

    private suspend fun generateDraft(mailId: String, replyType: MailReplyType): Result<MailReplyDto> =
        safeRequest {
            val reply = if (ApiConfig.USE_MOCK) {
                val existing = MockMailData.mailReplies.find { it.mailId == mailId && it.replyType == replyType }
                existing ?: MailReplyDto(
                    id = "mock-${replyType.name.lowercase()}-$mailId",
                    mailId = mailId,
                    replyType = replyType,
                    subject = if (replyType == MailReplyType.Quote) "RE: Fiyat Teklifi" else "RE: Ceviri Teslimi",
                    body = if (replyType == MailReplyType.Quote) {
                        "Sayin musterimiz,\n\nTalebinizi inceledik.\n\nOnayinizi bekliyoruz."
                    } else {
                        "Sayin musterimiz,\n\nTalep ettiginiz cevirinin onayli/damgali hali ekte yer almaktadir." +
                            "\n\nIyi calismalar dileriz."
                    },
                    isEdited = false,
                    attachmentFileName = null,
                    isApproved = false,
                    approvedAt = null,
                    sentAt = null,
                ).also { MockMailData.mailReplies.add(it) }
            } else {
                val path = if (replyType == MailReplyType.Quote) "quote" else "delivery"
                httpClient.post("/mails/$mailId/replies/$path").body()
            }
            ApiResponse(success = true, message = null, data = reply)
        }

    override suspend fun updateDraft(replyId: String, subject: String, body: String): Result<MailReplyDto> =
        safeRequest {
            val updated = if (ApiConfig.USE_MOCK) {
                val index = MockMailData.mailReplies.indexOfFirst { it.id == replyId }
                require(index >= 0) { "Yanit bulunamadi" }
                val reply = MockMailData.mailReplies[index].copy(subject = subject, body = body, isEdited = true)
                MockMailData.mailReplies[index] = reply
                reply
            } else {
                httpClient.put("/mail-replies/$replyId") {
                    contentType(ContentType.Application.Json)
                    setBody(UpdateReplyRequest(subject, body))
                }.body()
            }
            ApiResponse(success = true, message = null, data = updated)
        }

    override suspend fun attachDeliveryFile(replyId: String, fileName: String): Result<MailReplyDto> =
        safeRequest {
            // Henuz gercek bir dosya secici (file picker) uygulanmadi; bu yuzden USE_MOCK durumundan
            // bagimsiz olarak sadece dosya adini yerel olarak isaretliyoruz. Gercek multipart yukleme
            // (mail-replies/{id}/attachment) icin gercek dosya baytlari gerekir - ayri bir is kalemi.
            val index = MockMailData.mailReplies.indexOfFirst { it.id == replyId }
            require(index >= 0) { "Yanit bulunamadi" }
            val updated = MockMailData.mailReplies[index].copy(attachmentFileName = fileName)
            MockMailData.mailReplies[index] = updated
            ApiResponse(success = true, message = null, data = updated)
        }

    override suspend fun approve(replyId: String, actualPrice: Double?): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                val index = MockMailData.mailReplies.indexOfFirst { it.id == replyId }
                require(index >= 0) { "Yanit bulunamadi" }
                val updated = MockMailData.mailReplies[index].copy(
                    isApproved = true,
                    approvedAt = "2026-07-17T10:00:00Z",
                )
                MockMailData.mailReplies[index] = updated
            } else {
                httpClient.post("/mail-replies/$replyId/approve") {
                    contentType(ContentType.Application.Json)
                    setBody(ApproveReplyRequest(actualPrice))
                }
            }
            ApiResponse(success = true, message = null, data = Unit)
        }

    override suspend fun send(replyId: String): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                val index = MockMailData.mailReplies.indexOfFirst { it.id == replyId }
                require(index >= 0) { "Yanit bulunamadi" }
                val updated = MockMailData.mailReplies[index].copy(sentAt = "2026-07-17T10:00:00Z")
                MockMailData.mailReplies[index] = updated
            } else {
                httpClient.post("/mail-replies/$replyId/send")
            }
            ApiResponse(success = true, message = null, data = Unit)
        }
}
