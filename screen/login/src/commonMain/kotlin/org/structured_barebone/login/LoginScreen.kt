package org.structured_barebone.login

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.structured_barebone.login.resources.*
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.theme.AppLayout
import org.structured_barebone.theme.IconSize
import org.structured_barebone.theme.LineWidth
import org.structured_barebone.ui.AppTextField
import org.structured_barebone.ui.PasswordTextField
import org.structured_barebone.ui.ScreenBody
import org.structured_barebone.ui.messageResource
import org.structured_barebone.viewmodel.LoginViewModel

/** Email and password sign-in. On success the session opens and the app shell replaces this screen. */
@Composable
fun LoginScreen() {
    val viewModel: LoginViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScreenBody(maxWidth = AppLayout.formMaxWidth) {
        Text(stringResource(Res.string.login_title), style = MaterialTheme.typography.headlineMedium)
        Text(
            stringResource(Res.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AppTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = stringResource(Res.string.login_email),
            error = if (state.emailMissing) stringResource(Res.string.login_field_required) else null,
            enabled = !state.submitting,
            keyboardType = KeyboardType.Email,
        )
        PasswordTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = stringResource(Res.string.login_password),
            error = if (state.passwordMissing) stringResource(Res.string.login_field_required) else null,
            enabled = !state.submitting,
        )
        state.error?.let { error ->
            Text(stringResource(error.messageResource()), color = MaterialTheme.colorScheme.error)
        }
        Button(onClick = viewModel::submit, enabled = !state.submitting) {
            if (state.submitting) {
                CircularProgressIndicator(modifier = Modifier.size(IconSize.sm), strokeWidth = LineWidth.thick)
            } else {
                Text(stringResource(Res.string.login_submit))
            }
        }
    }
}

/** A rejected sign-in means wrong credentials, not an expired session, so it gets its own wording. */
private fun AppError.messageResource() = when (kind) {
    ErrorKind.UNAUTHORIZED -> Res.string.login_invalid_credentials
    else -> kind.messageResource()
}
