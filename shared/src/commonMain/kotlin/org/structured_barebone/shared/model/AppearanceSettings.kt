package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class AppearanceSettingsDto(
    val id: String,
    val brandText: String?,
    val brandLogoBase64: String?,
    val primaryColor: String,
    val fontFamily: String,
    val fontScale: Double,
    val updatedAt: String,
)
