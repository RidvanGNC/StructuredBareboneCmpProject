package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.CloudCredentialDto
import org.structured_barebone.shared.model.CloudProvider
import org.structured_barebone.shared.model.EngineMode
import org.structured_barebone.shared.model.EngineSettingsDto
import org.structured_barebone.shared.model.LlmRequestLogDto
import org.structured_barebone.shared.model.LocalModelDto
import org.structured_barebone.shared.model.Result

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
    ): Result<EngineSettingsDto>

    suspend fun getLocalModels(): Result<List<LocalModelDto>>

    suspend fun getCloudCredentials(): Result<List<CloudCredentialDto>>

    suspend fun upsertCloudCredential(provider: CloudProvider, apiKey: String): Result<CloudCredentialDto>

    suspend fun deleteCloudCredential(provider: CloudProvider): Result<Unit>

    suspend fun getLogs(take: Int = 100): Result<List<LlmRequestLogDto>>
}
