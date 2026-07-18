package org.structured_barebone.data.model

import kotlinx.serialization.Serializable
import org.structured_barebone.shared.model.CloudProvider
import org.structured_barebone.shared.model.EngineMode

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
data class UpsertPricingRateRequest(val languageCode: String, val ratePerCharacter: Double)

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
)

@Serializable
data class UpsertCloudCredentialRequest(val provider: CloudProvider, val apiKey: String)

@Serializable
data class SyncResultDto(val ingested: Int)
