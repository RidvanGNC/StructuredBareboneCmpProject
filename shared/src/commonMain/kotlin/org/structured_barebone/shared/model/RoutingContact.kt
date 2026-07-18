package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class RoutingContactDto(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val languagePreferences: List<String>,
    val addedAt: String,
    val removedAt: String?,
    val isActive: Boolean,
    val isAvailable: Boolean,
    val priority: Int,
)
