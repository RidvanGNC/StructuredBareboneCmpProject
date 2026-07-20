package org.structured_barebone.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class Currency {
    TRY, USD, EUR
}

@Serializable
data class PricingRateDto(
    val id: String,
    val languageCode: String,
    val ratePerCharacter: Double,
    val currency: Currency,
)
