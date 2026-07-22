package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class MailStatus {
    NotCompleted, InProgress, Completed
}

@Serializable
enum class MailType {
    Advertisement, Spam, InquiryOrOffer, Translation, Information, Social, Deleted
}

@Serializable
enum class LanguageSource {
    MailBody, Attachment
}

@Serializable
data class MailDto(
    val id: String,
    val userId: String,
    val customerId: String,
    val status: MailStatus,
    val type: MailType,
    val subject: String?,
    val body: String?,
    val predictedLanguage: String?,
    val confirmedLanguage: String?,
    val languageSource: LanguageSource,
    val isNotarized: Boolean,
    val completionDuration: String?,
    val customerGroup: Int,
    val mailGroup: String,
    val hasAttachment: Boolean,
    val attachmentFileName: String?,
    val sequenceNumber: Int,
    val isContinuation: Boolean,
    val isUrgent: Boolean,
    val attachmentCharacterCount: Int?,
    val predictedPrice: Double?,
    val actualPrice: Double?,
    val priceChanged: Boolean,
    val threadId: String,
    val receivedAt: String,
)

@Serializable
data class CustomerDto(
    val id: String,
    val name: String,
    val domainName: String,
    val isRecurring: Boolean,
    val isPotential: Boolean,
    val isApproved: Boolean,
    val isRejected: Boolean,
    val isBlacklisted: Boolean,
    val totalJobCount: Int,
    val successfulJobCount: Int,
    val ongoingJobCount: Int,
    val failedJobCount: Int,
)
