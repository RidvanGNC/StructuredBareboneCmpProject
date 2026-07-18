package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.RoutingContactDto

interface RoutingContactRepository {
    suspend fun getActive(): Result<List<RoutingContactDto>>
    suspend fun add(
        name: String,
        surname: String,
        email: String,
        languagePreferences: List<String>,
        isAvailable: Boolean,
        priority: Int,
    ): Result<RoutingContactDto>
    suspend fun update(
        id: String,
        name: String,
        surname: String,
        email: String,
        languagePreferences: List<String>,
        isAvailable: Boolean,
        priority: Int,
    ): Result<RoutingContactDto>
    suspend fun setAvailability(id: String, isAvailable: Boolean): Result<RoutingContactDto>
    suspend fun remove(id: String): Result<Unit>
}
