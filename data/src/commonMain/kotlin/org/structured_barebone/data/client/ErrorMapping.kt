package org.structured_barebone.data.client

import org.structured_barebone.data.model.ServerException
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.ErrorKind

fun errorKindForStatus(status: Int): ErrorKind = when (status) {
    400, 422 -> ErrorKind.VALIDATION
    401 -> ErrorKind.UNAUTHORIZED
    403 -> ErrorKind.FORBIDDEN
    404 -> ErrorKind.NOT_FOUND
    408, 504 -> ErrorKind.TIMEOUT
    409 -> ErrorKind.CONFLICT
    426 -> ErrorKind.UPDATE_REQUIRED
    429 -> ErrorKind.RATE_LIMITED
    in 500..599 -> ErrorKind.SERVER
    else -> ErrorKind.UNEXPECTED
}

fun ServerException.toAppError(): AppError = AppError(
    kind = errorKindForStatus(status),
    code = problem?.code,
    status = status,
    traceId = problem?.traceId,
    detail = problem?.detail ?: problem?.title,
    fieldErrors = problem?.errors.orEmpty(),
)
