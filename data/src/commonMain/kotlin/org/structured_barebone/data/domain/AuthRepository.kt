package org.structured_barebone.data.domain

import org.structured_barebone.data.model.UserProfile
import org.structured_barebone.shared.model.Result

interface AuthRepository {
    /** Signs in with email and password; on success the token pair is stored and the session is open. */
    suspend fun login(email: String, password: String): Result<Unit>

    /** The signed-in user. Also used to check that a stored session is still valid. */
    suspend fun profile(): Result<UserProfile>
}
