package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.MailReplyDto
import org.structured_barebone.shared.model.Result

interface MailReplyRepository {
    suspend fun getReplies(mailId: String): Result<List<MailReplyDto>>
    suspend fun generateQuoteDraft(mailId: String): Result<MailReplyDto>
    suspend fun generateDeliveryDraft(mailId: String): Result<MailReplyDto>
    suspend fun updateDraft(replyId: String, subject: String, body: String): Result<MailReplyDto>
    suspend fun attachDeliveryFile(replyId: String, fileName: String): Result<MailReplyDto>
    suspend fun approve(replyId: String, actualPrice: Double?): Result<Unit>
    suspend fun send(replyId: String): Result<Unit>
}
