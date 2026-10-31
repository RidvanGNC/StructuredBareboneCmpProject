package org.structured_barebone.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.stringResource
import org.structured_barebone.theme.Spacing
import org.structured_barebone.ui.resources.Res
import org.structured_barebone.ui.resources.action_cancel

/** Asks the user to confirm an action. */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_cancel)) } },
    )
}

/**
 * Confirmation for a destructive action: the confirm button stays disabled until the user types [phrase].
 */
@Composable
fun TypeToConfirmDialog(
    title: String,
    message: String,
    phrase: String,
    fieldLabel: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    var typed by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Text(message)
                AppTextField(value = typed, onValueChange = { typed = it }, label = fieldLabel)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = typed == phrase) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_cancel)) } },
    )
}
