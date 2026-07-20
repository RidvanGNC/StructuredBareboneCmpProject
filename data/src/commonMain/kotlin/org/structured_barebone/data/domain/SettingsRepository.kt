package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.AppearanceSettingsDto
import org.structured_barebone.shared.model.CloudCredentialDto
import org.structured_barebone.shared.model.CloudProvider
import org.structured_barebone.shared.model.EngineMode
import org.structured_barebone.shared.model.EngineSettingsDto
import org.structured_barebone.shared.model.LlmLogPageDto
import org.structured_barebone.shared.model.LocalModelDto
import org.structured_barebone.shared.model.MailType
import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.SystemNotificationDto
import org.structured_barebone.shared.model.VacationModeSettingsDto

data class LlmLogFilter(
    val page: Int = 1,
    val pageSize: Int = 100,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val mode: EngineMode? = null,
    val modelName: String? = null,
    val requestType: String? = null,
    val minTokens: Int? = null,
    val maxTokens: Int? = null,
    val success: Boolean? = null,
    val sortField: String? = null,
    val sortDescending: Boolean = true,
)

interface SettingsRepository {
    suspend fun getEngineConfig(): Result<EngineSettingsDto>

    suspend fun updateEngineConfig(
        mode: EngineMode,
        localModelName: String?,
        cloudProvider: CloudProvider?,
        cloudApiKey: String?,
        temperature: Double,
        maxTokens: Int?,
        topP: Double?,
        topK: Int?,
    ): Result<EngineSettingsDto>

    suspend fun getLocalModels(): Result<List<LocalModelDto>>

    suspend fun getCloudCredentials(): Result<List<CloudCredentialDto>>

    suspend fun upsertCloudCredential(provider: CloudProvider, apiKey: String): Result<CloudCredentialDto>

    suspend fun deleteCloudCredential(provider: CloudProvider): Result<Unit>

    suspend fun getLogs(filter: LlmLogFilter = LlmLogFilter()): Result<LlmLogPageDto>

    suspend fun getAppearance(): Result<AppearanceSettingsDto>

    suspend fun updateAppearance(
        brandText: String?,
        brandLogoBase64: String?,
        primaryColor: String,
        fontFamily: String,
        fontScale: Double,
    ): Result<AppearanceSettingsDto>

    suspend fun getVacationMode(): Result<VacationModeSettingsDto>

    suspend fun updateVacationMode(
        isEnabled: Boolean,
        messageTemplate: String,
        notifyMailTypes: List<MailType>,
    ): Result<VacationModeSettingsDto>

    suspend fun getSystemNotifications(): Result<List<SystemNotificationDto>>
}
