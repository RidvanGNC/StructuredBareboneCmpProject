package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class ManagedMailGroupDto(
    val id: String,
    val mailGroup: String,
    val isActive: Boolean,
    val note: String?,
    val addedAt: String,
)
