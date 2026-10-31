package org.structured_barebone.data.model

import kotlinx.serialization.Serializable

/**
 * Error body of every failed API response (RFC 9457, `application/problem+json`).
 * `code` and `traceId` are extension members defined in `contracts/common.openapi.yaml`.
 */
@Serializable
data class ProblemDetails(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val detail: String? = null,
    val instance: String? = null,
    val code: String? = null,
    val traceId: String? = null,
    val errors: Map<String, List<String>>? = null,
)
