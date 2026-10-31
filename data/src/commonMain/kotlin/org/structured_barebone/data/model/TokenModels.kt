package org.structured_barebone.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(val refreshToken: String)

@Serializable
data class TokenPair(val accessToken: String, val refreshToken: String)
