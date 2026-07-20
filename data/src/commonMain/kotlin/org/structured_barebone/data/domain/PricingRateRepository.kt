package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.Currency
import org.structured_barebone.shared.model.PricingRateDto
import org.structured_barebone.shared.model.Result

interface PricingRateRepository {
    suspend fun getAll(): Result<List<PricingRateDto>>
    suspend fun upsert(languageCode: String, ratePerCharacter: Double, currency: Currency): Result<PricingRateDto>
    suspend fun remove(id: String): Result<Unit>
}
