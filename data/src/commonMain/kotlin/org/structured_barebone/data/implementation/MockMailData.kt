package org.structured_barebone.data.implementation

import org.structured_barebone.shared.model.CloudCredentialDto
import org.structured_barebone.shared.model.CloudProvider
import org.structured_barebone.shared.model.CustomerDto
import org.structured_barebone.shared.model.EngineMode
import org.structured_barebone.shared.model.EngineSettingsDto
import org.structured_barebone.shared.model.LanguageSource
import org.structured_barebone.shared.model.LlmRequestLogDto
import org.structured_barebone.shared.model.LocalModelDto
import org.structured_barebone.shared.model.MailDto
import org.structured_barebone.shared.model.MailReplyDto
import org.structured_barebone.shared.model.MailReplyType
import org.structured_barebone.shared.model.MailStatus
import org.structured_barebone.shared.model.MailType
import org.structured_barebone.shared.model.PricingRateDto
import org.structured_barebone.shared.model.RoutingContactDto

// Web tarafindaki mockData.ts ile tutarli ornek veri; gercek backend baglanana kadar kullanilir.
internal object MockMailData {
    val customers = listOf(
        CustomerDto(
            id = "c1", name = "Acme Hukuk Burosu", domainName = "acmehukuk.com",
            isRecurring = true, isPotential = false, isApproved = true,
            isRejected = false, isBlacklisted = false,
            totalJobCount = 42, successfulJobCount = 40, ongoingJobCount = 1, failedJobCount = 1,
        ),
        CustomerDto(
            id = "c2", name = "Baltık Ticaret A.Ş.", domainName = "baltikticaret.com",
            isRecurring = false, isPotential = true, isApproved = false,
            isRejected = false, isBlacklisted = false,
            totalJobCount = 2, successfulJobCount = 1, ongoingJobCount = 1, failedJobCount = 0,
        ),
    )

    val mails = listOf(
        MailDto(
            id = "m1", userId = "u1", customerId = "c2",
            status = MailStatus.NotCompleted, type = MailType.InquiryOrOffer,
            predictedLanguage = "en", confirmedLanguage = null, languageSource = LanguageSource.Attachment,
            isNotarized = true, completionDuration = null, customerGroup = 2, mailGroup = "baltikticaret",
            hasAttachment = true, sequenceNumber = 0, isContinuation = false, isUrgent = true,
            attachmentCharacterCount = 4200, predictedPrice = 210.0, actualPrice = null, priceChanged = false,
            threadId = "t1", receivedAt = "2026-07-16T16:12:00Z",
        ),
        MailDto(
            id = "m2", userId = "u1", customerId = "c1",
            status = MailStatus.InProgress, type = MailType.Translation,
            predictedLanguage = "de", confirmedLanguage = "de", languageSource = LanguageSource.Attachment,
            isNotarized = false, completionDuration = null, customerGroup = 1, mailGroup = "acmehukuk",
            hasAttachment = true, sequenceNumber = 0, isContinuation = false, isUrgent = false,
            attachmentCharacterCount = 9800, predictedPrice = null, actualPrice = null, priceChanged = false,
            threadId = "t2", receivedAt = "2026-07-16T12:12:00Z",
        ),
        MailDto(
            id = "m3", userId = "u1", customerId = "c1",
            status = MailStatus.Completed, type = MailType.Translation,
            predictedLanguage = "fr", confirmedLanguage = "fr", languageSource = LanguageSource.Attachment,
            isNotarized = true, completionDuration = "1.04:12:00", customerGroup = 1, mailGroup = "acmehukuk",
            hasAttachment = true, sequenceNumber = 0, isContinuation = false, isUrgent = false,
            attachmentCharacterCount = 15320, predictedPrice = null, actualPrice = 480.0, priceChanged = true,
            threadId = "t3", receivedAt = "2026-07-14T17:12:00Z",
        ),
        MailDto(
            id = "m4", userId = "u1", customerId = "c2",
            status = MailStatus.NotCompleted, type = MailType.Spam,
            predictedLanguage = "en", confirmedLanguage = null, languageSource = LanguageSource.MailBody,
            isNotarized = false, completionDuration = null, customerGroup = 3, mailGroup = "baltikticaret",
            hasAttachment = false, sequenceNumber = 0, isContinuation = false, isUrgent = false,
            attachmentCharacterCount = null, predictedPrice = null, actualPrice = null, priceChanged = false,
            threadId = "t4", receivedAt = "2026-07-16T14:12:00Z",
        ),
        MailDto(
            id = "m5", userId = "u1", customerId = "c2",
            status = MailStatus.NotCompleted, type = MailType.Advertisement,
            predictedLanguage = "tr", confirmedLanguage = null, languageSource = LanguageSource.MailBody,
            isNotarized = false, completionDuration = null, customerGroup = 3, mailGroup = "baltikticaret",
            hasAttachment = false, sequenceNumber = 0, isContinuation = false, isUrgent = false,
            attachmentCharacterCount = null, predictedPrice = null, actualPrice = null, priceChanged = false,
            threadId = "t5", receivedAt = "2026-07-15T11:12:00Z",
        ),
        MailDto(
            id = "m6", userId = "u1", customerId = "c1",
            status = MailStatus.InProgress, type = MailType.Translation,
            predictedLanguage = "de", confirmedLanguage = "de", languageSource = LanguageSource.MailBody,
            isNotarized = false, completionDuration = null, customerGroup = 1, mailGroup = "acmehukuk",
            hasAttachment = false, sequenceNumber = 1, isContinuation = true, isUrgent = false,
            attachmentCharacterCount = null, predictedPrice = null, actualPrice = null, priceChanged = false,
            threadId = "t2", receivedAt = "2026-07-16T15:12:00Z",
        ),
    )

    val mailReplies = mutableListOf(
        MailReplyDto(
            id = "r1", mailId = "m1", replyType = MailReplyType.Quote,
            subject = "RE: Fiyat Teklifi",
            body = "Sayin Baltik Ticaret,\n\nTespit edilen dil: en.\nTahmini ceviri ucreti: 210.00.\n\nOnayinizi bekliyoruz.",
            isEdited = false, attachmentFileName = null, isApproved = false, approvedAt = null, sentAt = null,
        ),
    )

    val routingContacts = mutableListOf(
        RoutingContactDto(
            id = "rc1", name = "Elif", surname = "Demir", email = "elif.demir@example.com",
            languagePreferences = listOf("en", "de"), addedAt = "2025-08-17T00:00:00Z",
            removedAt = null, isActive = true, isAvailable = true, priority = 0,
        ),
        RoutingContactDto(
            id = "rc2", name = "Mert", surname = "Yildiz", email = "mert.yildiz@example.com",
            languagePreferences = listOf("en"), addedAt = "2025-10-01T00:00:00Z",
            removedAt = null, isActive = true, isAvailable = false, priority = 1,
        ),
    )

    val pricingRates = mutableListOf(
        PricingRateDto(id = "p1", languageCode = "en", ratePerCharacter = 0.05),
        PricingRateDto(id = "p2", languageCode = "de", ratePerCharacter = 0.06),
        PricingRateDto(id = "p3", languageCode = "fr", ratePerCharacter = 0.06),
    )

    var engineSettings = EngineSettingsDto(
        id = "es1", mode = EngineMode.Local, localModelName = "llama3.1:8b",
        cloudProvider = CloudProvider.Claude, temperature = 0.0, maxTokens = null, topP = null,
        updatedAt = "2026-07-16T10:00:00Z",
    )

    val localModels = listOf(
        LocalModelDto(name = "llama3.1:8b", sizeBytes = 4_920_000_000, modifiedAt = "2026-07-09T13:33:00Z"),
        LocalModelDto(name = "llama3.2", sizeBytes = 2_000_000_000, modifiedAt = "2026-06-27T01:33:00Z"),
    )

    val cloudCredentials = mutableListOf(
        CloudCredentialDto(id = "cc1", provider = CloudProvider.Claude, maskedPreview = "****VwCv", updatedAt = "2026-07-05T10:00:00Z"),
    )

    val llmLogs = listOf(
        LlmRequestLogDto(
            id = "log1", timestamp = "2026-07-17T20:33:41Z", mode = EngineMode.Local,
            modelName = "llama3.1:8b", requestType = "mail_analysis",
            promptContent = "Konu: Fiyat teklifi\n\nIcerik:\nMerhaba, ceviri fiyati almak istiyorum.",
            responseContent = "{\"detected_language\":\"tr\",\"mail_type\":\"soru_ve_teklif\"}",
            promptTokens = 120, completionTokens = 40, totalTokens = 160,
            success = true, errorMessage = null, durationMs = 850,
        ),
    )
}
