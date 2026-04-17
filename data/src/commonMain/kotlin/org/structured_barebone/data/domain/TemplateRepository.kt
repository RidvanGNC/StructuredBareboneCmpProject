package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.Result

interface TemplateRepository {
    suspend fun template(): Result<String>
}