package org.structured_barebone.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean, val message: String? = null, val data: T? = null
)

@Serializable
data class ProblemDetails(
    val title: String? = null,
    val status: Int? = null,
    val errors: Map<String, List<String>>? = null
) {
    fun toErrorMessage(): String {
        return errors?.values?.flatten()?.joinToString("\n")
            ?: title
            ?: "Beklenmeyen bir hata oluştu."
    }
}

@Serializable
class ServerException(val code: Int, val description: String) : Exception(description)