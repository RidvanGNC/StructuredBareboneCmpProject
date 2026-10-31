package org.structured_barebone.data.implementation

import org.structured_barebone.data.domain.TemplateRepository
import org.structured_barebone.shared.model.Result

class TemplateRepositoryImpl : BaseRepository(), TemplateRepository {
    override suspend fun template(): Result<String> = safeCall { "Template Data" }
}
