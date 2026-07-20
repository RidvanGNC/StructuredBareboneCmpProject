package org.structured_barebone.data.model

import kotlinx.serialization.Serializable
import org.structured_barebone.shared.model.CloudProvider
import org.structured_barebone.shared.model.Currency
import org.structured_barebone.shared.model.EngineMode
import org.structured_barebone.shared.model.MailType

@Serializable
data class UpdateReplyRequest(val subject: String, val body: String)

@Serializable
data class ApproveReplyRequest(val actualPrice: Double?)

@Serializable
data class AddRoutingContactRequest(
    val name: String,
    val surname: String,
    val email: String,
    val languagePreferences: List<String>,
    val isAvailable: Boolean,
    val priority: Int,
)

@Serializable
data class UpsertPricingRateRequest(val languageCode: String, val ratePerCharacter: Double, val currency: Currency)

@Serializable
data class UpdateRoutingContactRequest(
    val name: String,
    val surname: String,
    val email: String,
    val languagePreferences: List<String>,
    val isAvailable: Boolean,
    val priority: Int,
)

@Serializable
data class SetRoutingContactAvailabilityRequest(val isAvailable: Boolean)

@Serializable
data class UpdateEngineConfigRequest(
    val mode: EngineMode,
    val localModelName: String?,
    val cloudProvider: CloudProvider?,
    val cloudApiKey: String?,
    val temperature: Double,
    val maxTokens: Int?,
    val topP: Double?,
    val topK: Int?,
)

@Serializable
data class UpsertCloudCredentialRequest(val provider: CloudProvider, val apiKey: String)

@Serializable
data class SyncResultDto(val ingested: Int)

@Serializable
data class ConfirmLanguageRequest(val language: String)

@Serializable
data class AddManagedMailGroupRequest(val mailGroup: String)

@Serializable
data class SetMailGroupActiveRequest(val isActive: Boolean)

@Serializable
data class SetMailGroupNoteRequest(val note: String?)

@Serializable
data class UpdateAppearanceRequest(
    val brandText: String?,
    val brandLogoBase64: String?,
    val primaryColor: String,
    val fontFamily: String,
    val fontScale: Double,
)

@Serializable
data class UpdateVacationModeRequest(
    val isEnabled: Boolean,
    val messageTemplate: String,
    val notifyMailTypes: List<MailType>,
)
