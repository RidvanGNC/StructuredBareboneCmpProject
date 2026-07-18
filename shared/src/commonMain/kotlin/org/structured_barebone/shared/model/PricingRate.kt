package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class PricingRateDto(
    val id: String,
    val languageCode: String,
    val ratePerCharacter: Double,
)
