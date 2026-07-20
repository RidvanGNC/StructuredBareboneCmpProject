package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.ManagedMailGroupDto
import org.structured_barebone.shared.model.Result

interface ManagedMailGroupRepository {
    suspend fun getAll(): Result<List<ManagedMailGroupDto>>
    suspend fun add(mailGroup: String): Result<ManagedMailGroupDto>
    suspend fun setActive(id: String, isActive: Boolean): Result<ManagedMailGroupDto>
    suspend fun setNote(id: String, note: String?): Result<ManagedMailGroupDto>
    suspend fun delete(id: String): Result<Unit>
}
