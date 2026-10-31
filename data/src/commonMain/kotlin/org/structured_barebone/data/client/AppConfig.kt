package org.structured_barebone.data.client

/**
 * Deployment settings supplied by the host application.
 *
 * @property apiBaseUrl root of the backend API. When it is null or blank the app runs without a backend: the
 * sign-in gate is off and screens show fixture data.
 */
data class AppConfig(val apiBaseUrl: String? = null) {
    val authEnabled: Boolean get() = !apiBaseUrl.isNullOrBlank()

    /** Absolute URL of an API [path] such as `/v1/auth/me`. */
    fun url(path: String): String {
        val base = requireNotNull(apiBaseUrl?.takeIf { it.isNotBlank() }) { "The API base URL is not configured." }
        return base.trimEnd('/') + path
    }
}

/** Paths of the authentication endpoints; see `contracts/common.openapi.yaml`. */
object AuthPaths {
    const val LOGIN = "/v1/auth/login"
    const val REFRESH = "/v1/auth/refresh"
    const val LOGOUT = "/v1/auth/logout"
    const val ME = "/v1/auth/me"
}
