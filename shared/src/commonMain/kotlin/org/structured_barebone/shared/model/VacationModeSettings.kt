package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class VacationModeSettingsDto(
    val id: String,
    val isEnabled: Boolean,
    val messageTemplate: String,
    val notifyMailTypes: List<MailType>,
    val updatedAt: String,
)
