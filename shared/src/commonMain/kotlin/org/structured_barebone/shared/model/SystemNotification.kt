package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class SystemNotificationDto(
    val id: String,
    val type: String,
    val severity: String,
    val detail: String?,
    val timestamp: String,
)
