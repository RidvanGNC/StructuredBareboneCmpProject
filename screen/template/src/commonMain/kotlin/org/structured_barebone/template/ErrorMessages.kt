package org.structured_barebone.template

import org.jetbrains.compose.resources.StringResource
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.template.resources.*

/** Maps an error category to its localized message. Add a case here when [ErrorKind] gets a new entry. */
internal fun ErrorKind.messageResource(): StringResource = when (this) {
    ErrorKind.NETWORK_UNREACHABLE -> Res.string.error_network_unreachable
    ErrorKind.TIMEOUT -> Res.string.error_timeout
    ErrorKind.UNAUTHORIZED -> Res.string.error_unauthorized
    ErrorKind.FORBIDDEN -> Res.string.error_forbidden
    ErrorKind.NOT_FOUND -> Res.string.error_not_found
    ErrorKind.CONFLICT -> Res.string.error_conflict
    ErrorKind.VALIDATION -> Res.string.error_validation
    ErrorKind.RATE_LIMITED -> Res.string.error_rate_limited
    ErrorKind.UPDATE_REQUIRED -> Res.string.error_update_required
    ErrorKind.SERVER -> Res.string.error_server
    ErrorKind.UNEXPECTED -> Res.string.error_unexpected
}
