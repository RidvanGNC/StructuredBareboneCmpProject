package org.structured_barebone.shared.model

/**
 * Broad failure categories. The UI maps a kind (and optionally [AppError.code]) to localized text;
 * no user-visible text is produced below the presentation layer.
 */
enum class ErrorKind {
    NETWORK_UNREACHABLE,
    TIMEOUT,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    CONFLICT,
    VALIDATION,
    RATE_LIMITED,
    UPDATE_REQUIRED,
    SERVER,
    UNEXPECTED,
}

/**
 * A failure described by data only.
 *
 * @property code stable machine code from the server problem document, if any.
 * @property detail technical text for logs or a "copy details" action; never show it directly.
 * @property traceId server correlation id for support.
 * @property fieldErrors validation messages by field name, as sent by the server.
 */
data class AppError(
    val kind: ErrorKind,
    val code: String? = null,
    val status: Int? = null,
    val traceId: String? = null,
    val detail: String? = null,
    val fieldErrors: Map<String, List<String>> = emptyMap(),
) {
    /** True when the server could not be reached at all, so the action may be queued or retried later. */
    val isOffline: Boolean get() = kind == ErrorKind.NETWORK_UNREACHABLE
}
