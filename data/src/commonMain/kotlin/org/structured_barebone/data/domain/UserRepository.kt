package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.Result
import org.structured_barebone.shared.model.UserProfile

interface UserRepository {
    suspend fun getCurrentUser(): Result<UserProfile>
    suspend fun requestAccountDeletion(): Result<Unit>
}
