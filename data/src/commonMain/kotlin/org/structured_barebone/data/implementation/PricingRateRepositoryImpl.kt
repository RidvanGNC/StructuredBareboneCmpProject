package org.structured_barebone.data.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.structured_barebone.data.client.ApiConfig
import org.structured_barebone.data.domain.PricingRateRepository
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.UpsertPricingRateRequest
import org.structured_barebone.shared.model.Currency
import org.structured_barebone.shared.model.PricingRateDto
import org.structured_barebone.shared.model.Result

class PricingRateRepositoryImpl(
    private val httpClient: HttpClient,
) : BaseRepository(), PricingRateRepository {

    override suspend fun getAll(): Result<List<PricingRateDto>> =
        safeRequest {
            val rates = if (ApiConfig.USE_MOCK) {
                MockMailData.pricingRates.toList()
            } else {
                httpClient.get("/pricing-rates").body()
            }
            ApiResponse(success = true, message = null, data = rates)
        }

    override suspend fun upsert(languageCode: String, ratePerCharacter: Double, currency: Currency): Result<PricingRateDto> =
        safeRequest {
            val rate = if (ApiConfig.USE_MOCK) {
                val index = MockMailData.pricingRates.indexOfFirst { it.languageCode == languageCode }
                if (index >= 0) {
                    val updated = MockMailData.pricingRates[index].copy(ratePerCharacter = ratePerCharacter, currency = currency)
                    MockMailData.pricingRates[index] = updated
                    updated
                } else {
                    val created = PricingRateDto(
                        id = "mock-rate-$languageCode",
                        languageCode = languageCode,
                        ratePerCharacter = ratePerCharacter,
                        currency = currency,
                    )
                    MockMailData.pricingRates.add(created)
                    created
                }
            } else {
                httpClient.put("/pricing-rates") {
                    contentType(ContentType.Application.Json)
                    setBody(UpsertPricingRateRequest(languageCode, ratePerCharacter, currency))
                }.body()
            }
            ApiResponse(success = true, message = null, data = rate)
        }

    override suspend fun remove(id: String): Result<Unit> =
        safeRequest {
            if (ApiConfig.USE_MOCK) {
                val index = MockMailData.pricingRates.indexOfFirst { it.id == id }
                require(index >= 0) { "Kayit bulunamadi" }
                MockMailData.pricingRates.removeAt(index)
            } else {
                httpClient.delete("/pricing-rates/$id")
            }
            ApiResponse(success = true, message = null, data = Unit)
        }
}
