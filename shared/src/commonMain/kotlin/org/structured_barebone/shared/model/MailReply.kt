package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class MailReplyType {
    Quote, Delivery
}

@Serializable
data class MailReplyDto(
    val id: String,
    val mailId: String,
    val replyType: MailReplyType,
    val subject: String,
    val body: String,
    val isEdited: Boolean,
    val attachmentFileName: String?,
    val isApproved: Boolean,
    val approvedAt: String?,
    val sentAt: String?,
)
