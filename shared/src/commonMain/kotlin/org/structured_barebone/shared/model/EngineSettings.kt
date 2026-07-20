package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class EngineMode {
    Local,
    Cloud,
}

@Serializable
enum class CloudProvider {
    Claude,
    Gemini,
    OpenAi,
}

@Serializable
data class EngineSettingsDto(
    val id: String,
    val mode: EngineMode,
    val localModelName: String,
    val cloudProvider: CloudProvider,
    val temperature: Double,
    val maxTokens: Int?,
    val topP: Double?,
    val updatedAt: String,
)

@Serializable
data class LocalModelDto(
    val name: String,
    val sizeBytes: Long,
    val modifiedAt: String?,
)

@Serializable
data class CloudCredentialDto(
    val id: String,
    val provider: CloudProvider,
    val maskedPreview: String,
    val updatedAt: String,
)

@Serializable
data class LlmRequestLogDto(
    val id: String,
    val timestamp: String,
    val mode: EngineMode,
    val modelName: String,
    val requestType: String,
    val promptContent: String,
    val responseContent: String?,
    val promptTokens: Int?,
    val completionTokens: Int?,
    val totalTokens: Int?,
    val success: Boolean,
    val errorMessage: String?,
    val durationMs: Int,
)

@Serializable
data class LlmLogPageDto(
    val items: List<LlmRequestLogDto>,
    val totalCount: Int,
)
