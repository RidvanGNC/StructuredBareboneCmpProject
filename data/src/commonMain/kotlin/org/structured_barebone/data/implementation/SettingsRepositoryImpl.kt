package org.structured_barebone.data.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.structured_barebone.data.client.ApiConfig
import org.structured_barebone.data.domain.LlmLogFilter
import org.structured_barebone.data.domain.SettingsRepository
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.UpdateAppearanceRequest
import org.structured_barebone.data.model.UpdateEngineConfigRequest
import org.structured_barebone.data.model.UpdateVacationModeRequest
import org.structured_barebone.data.model.UpsertCloudCredentialRequest
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

class SettingsRepositoryImpl(
    private val httpClient: HttpClient,
) : BaseRepository(), SettingsRepository {

    override suspend fun getEngineConfig(): Result<EngineSettingsDto> =
        safeRequest {
            val settings = if (ApiConfig.USE_MOCK) {
                MockMailData.engineSettings
            } else {
                httpClient.get("/settings/engine-config").body()
            }
            ApiResponse(success = true, message = null, data = settings)
        }

    override suspend fun updateEngineConfig(
        mode: EngineMode,
        localModelName: String?,
        cloudProvider: CloudProvider?,
        cloudApiKey: String?,
        temperature: Double,
        maxTokens: Int?,
        topP: Double?,
        topK: Int?,
    ): Result<EngineSettingsDto> =
        safeRequest {
            val settings = if (ApiConfig.USE_MOCK) {
                val updated = MockMailData.engineSettings.copy(
                    mode = mode,
                    localModelName = localModelName ?: MockMailData.engineSettings.localModelName,
                    cloudProvider = cloudProvider ?: MockMailData.engineSettings.cloudProvider,
                    temperature = temperature,
                    maxTokens = maxTokens,
                    topP = topP,
                    topK = topK,
                )
                MockMailData.engineSettings = updated
                updated
            } else {
                httpClient.put("/settings/engine-config") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        UpdateEngineConfigRequest(
                            mode, localModelName, cloudProvider, cloudApiKey, temperature, maxTokens, topP, topK,
                        ),
                    )
                }.body()
            }
            ApiResponse(success = true, message = null, data = settings)
        }

    override suspend fun getLocalModels(): Result<List<LocalModelDto>> =
        safeRequest {
            val models = if (ApiConfig.USE_MOCK) {
                MockMailData.localModels
            } else {
                httpClient.get("/settings/local-models").body()
            }
            ApiResponse(success = true, message = null, data = models)
        }

    override suspend fun getCloudCredentials(): Result<List<CloudCredentialDto>> =
        safeRequest {
            val credentials = if (ApiConfig.USE_MOCK) {
                MockMailData.cloudCredentials.toList()
            } else {
                httpClient.get("/settings/cloud-credentials").body()
            }
            ApiResponse(success = true, message = null, data = credentials)
        }

    override suspend fun upsertCloudCredential(
        provider: CloudProvider,
        apiKey: String,
    ): Result<CloudCredentialDto> =
        safeRequest {
            val credential = if (ApiConfig.USE_MOCK) {
                val masked = if (apiKey.length <= 4) "****" else "****${apiKey.takeLast(4)}"
                val index = MockMailData.cloudCredentials.indexOfFirst { it.provider == provider }
                if (index >= 0) {
                    val updated = MockMailData.cloudCredentials[index].copy(maskedPreview = masked)
                    MockMailData.cloudCredentials[index] = updated
                    updated
                } else {
                    val created = CloudCredentialDto(
                        id = "mock-cred-$provider", provider = provider,
                        maskedPreview = masked, updatedAt = "2026-07-17T10:00:00Z",
                    )
                    MockMailData.cloudCredentials.add(created)
                    created
                }
            } else {
                httpClient.post("/settings/cloud-credentials") {
                    contentType(ContentType.Application.Json)
                    setBody(UpsertCloudCredentialRequest(provider, apiKey))
                }.body()
            }
            ApiResponse(success = true, message = null, data = credential)
        }

    override suspend fun deleteCloudCredential(provider: CloudProvider): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                MockMailData.cloudCredentials.removeAll { it.provider == provider }
            } else {
                httpClient.delete("/settings/cloud-credentials/$provider")
            }
            ApiResponse(success = true, message = null, data = Unit)
        }

    override suspend fun getLogs(filter: LlmLogFilter): Result<LlmLogPageDto> =
        safeRequest {
            val page = if (ApiConfig.USE_MOCK) {
                LlmLogPageDto(items = MockMailData.llmLogs, totalCount = MockMailData.llmLogs.size)
            } else {
                httpClient.get("/settings/logs") {
                    parameter("page", filter.page)
                    parameter("pageSize", filter.pageSize)
                    parameter("dateFrom", filter.dateFrom)
                    parameter("dateTo", filter.dateTo)
                    parameter("mode", filter.mode)
                    parameter("modelName", filter.modelName)
                    parameter("requestType", filter.requestType)
                    parameter("minTokens", filter.minTokens)
                    parameter("maxTokens", filter.maxTokens)
                    parameter("success", filter.success)
                    parameter("sortField", filter.sortField)
                    parameter("sortDescending", filter.sortDescending)
                }.body()
            }
            ApiResponse(success = true, message = null, data = page)
        }

    override suspend fun getAppearance(): Result<AppearanceSettingsDto> =
        safeRequest {
            val settings = if (ApiConfig.USE_MOCK) {
                MockMailData.appearanceSettings
            } else {
                httpClient.get("/settings/appearance").body()
            }
            ApiResponse(success = true, message = null, data = settings)
        }

    override suspend fun updateAppearance(
        brandText: String?,
        brandLogoBase64: String?,
        primaryColor: String,
        fontFamily: String,
        fontScale: Double,
    ): Result<AppearanceSettingsDto> =
        safeRequest {
            val settings = if (ApiConfig.USE_MOCK) {
                val updated = MockMailData.appearanceSettings.copy(
                    brandText = brandText,
                    brandLogoBase64 = brandLogoBase64,
                    primaryColor = primaryColor,
                    fontFamily = fontFamily,
                    fontScale = fontScale,
                )
                MockMailData.appearanceSettings = updated
                updated
            } else {
                httpClient.put("/settings/appearance") {
                    contentType(ContentType.Application.Json)
                    setBody(UpdateAppearanceRequest(brandText, brandLogoBase64, primaryColor, fontFamily, fontScale))
                }.body()
            }
            ApiResponse(success = true, message = null, data = settings)
        }

    override suspend fun getVacationMode(): Result<VacationModeSettingsDto> =
        safeRequest {
            val settings = if (ApiConfig.USE_MOCK) {
                MockMailData.vacationModeSettings
            } else {
                httpClient.get("/settings/vacation-mode").body()
            }
            ApiResponse(success = true, message = null, data = settings)
        }

    override suspend fun updateVacationMode(
        isEnabled: Boolean,
        messageTemplate: String,
        notifyMailTypes: List<MailType>,
    ): Result<VacationModeSettingsDto> =
        safeRequest {
            val settings = if (ApiConfig.USE_MOCK) {
                val updated = MockMailData.vacationModeSettings.copy(
                    isEnabled = isEnabled,
                    messageTemplate = messageTemplate,
                    notifyMailTypes = notifyMailTypes,
                )
                MockMailData.vacationModeSettings = updated
                updated
            } else {
                httpClient.put("/settings/vacation-mode") {
                    contentType(ContentType.Application.Json)
                    setBody(UpdateVacationModeRequest(isEnabled, messageTemplate, notifyMailTypes))
                }.body()
            }
            ApiResponse(success = true, message = null, data = settings)
        }

    override suspend fun getSystemNotifications(): Result<List<SystemNotificationDto>> =
        safeRequest {
            val notifications = if (ApiConfig.USE_MOCK) {
                emptyList<SystemNotificationDto>()
            } else {
                httpClient.get("/settings/system-notifications").body<List<SystemNotificationDto>>()
            }
            ApiResponse(success = true, message = null, data = notifications)
        }
}
