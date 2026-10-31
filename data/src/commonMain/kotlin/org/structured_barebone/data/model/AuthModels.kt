package org.structured_barebone.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

/** The signed-in user as the server describes them. */
@Serializable
data class UserProfile(val id: String, val email: String, val displayName: String? = null)
