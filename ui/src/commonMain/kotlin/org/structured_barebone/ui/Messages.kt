package org.structured_barebone.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.structured_barebone.shared.model.AppLanguage
import org.structured_barebone.shared.model.AppMessage
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.shared.model.MessageSeverity
import org.structured_barebone.shared.model.ThemeMode
import org.structured_barebone.theme.Spacing
import org.structured_barebone.ui.resources.*

/** Maps an error category to its localized message. Add a case here when [ErrorKind] gets a new entry. */
fun ErrorKind.messageResource(): StringResource = when (this) {
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

fun AppLanguage.labelResource(): StringResource = when (this) {
    AppLanguage.SYSTEM -> Res.string.language_system
    AppLanguage.ENGLISH -> Res.string.language_english
    AppLanguage.TURKISH -> Res.string.language_turkish
}

fun ThemeMode.labelResource(): StringResource = when (this) {
    ThemeMode.SYSTEM -> Res.string.theme_system
    ThemeMode.LIGHT -> Res.string.theme_light
    ThemeMode.DARK -> Res.string.theme_dark
}

/**
 * The message strip: shows an [AppMessage] with its localized text and the server trace id (support reference).
 */
@Composable
fun MessageBanner(message: AppMessage, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val colors = when (message.severity) {
        MessageSeverity.INFO -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        MessageSeverity.WARNING -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        MessageSeverity.ERROR -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    }
    Card(modifier = modifier.fillMaxWidth(), colors = colors) {
        Row(
            modifier = Modifier.padding(start = Spacing.lg, top = Spacing.sm, bottom = Spacing.sm, end = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(stringResource(message.error.kind.messageResource()), style = MaterialTheme.typography.bodyMedium)
                message.error.traceId?.let {
                    Text(stringResource(Res.string.message_reference, it), style = MaterialTheme.typography.bodySmall)
                }
            }
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_dismiss)) }
        }
    }
}
