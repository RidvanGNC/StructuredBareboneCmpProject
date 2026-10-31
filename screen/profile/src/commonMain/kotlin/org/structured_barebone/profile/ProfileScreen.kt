package org.structured_barebone.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.structured_barebone.data.model.UserProfile
import org.structured_barebone.profile.resources.*
import org.structured_barebone.shared.model.AppLanguage
import org.structured_barebone.shared.model.ThemeMode
import org.structured_barebone.ui.AvatarCircle
import org.structured_barebone.ui.ConfirmDialog
import org.structured_barebone.ui.ListRow
import org.structured_barebone.ui.RowDivider
import org.structured_barebone.ui.ScreenBody
import org.structured_barebone.ui.ScreenHeader
import org.structured_barebone.ui.SectionCard
import org.structured_barebone.ui.SectionHeader
import org.structured_barebone.ui.StateMessage
import org.structured_barebone.ui.labelResource
import org.structured_barebone.ui.messageResource
import org.structured_barebone.viewmodel.AppearanceViewModel
import org.structured_barebone.viewmodel.LanguageViewModel
import org.structured_barebone.viewmodel.ProfileViewModel

/**
 * Account, appearance and language settings in grouped lists.
 *
 * @param onSignOut called after the user confirmed signing out; the app shell owns the session.
 */
@Composable
fun ProfileScreen(onSignOut: () -> Unit) {
    val profileViewModel: ProfileViewModel = koinViewModel()
    val appearanceViewModel: AppearanceViewModel = koinViewModel()
    val languageViewModel: LanguageViewModel = koinViewModel()
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()
    val themeMode by appearanceViewModel.themeMode.collectAsStateWithLifecycle()
    val language by languageViewModel.language.collectAsStateWithLifecycle()
    var confirmSignOut by remember { mutableStateOf(false) }

    Column {
        ScreenHeader(stringResource(Res.string.profile_title))
        ScreenBody {
            when (val current = profileState) {
                ProfileViewModel.UiState.Loading -> Unit
                ProfileViewModel.UiState.NoAccount -> Text(
                    stringResource(Res.string.profile_no_account),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                is ProfileViewModel.UiState.Success -> AccountSection(current.profile)
                is ProfileViewModel.UiState.Error -> StateMessage(
                    title = stringResource(current.error.kind.messageResource()),
                    onAction = profileViewModel::refresh,
                )
            }

            Column {
                SectionHeader(stringResource(Res.string.profile_section_appearance))
                SectionCard {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        if (index > 0) RowDivider()
                        ChoiceRow(stringResource(mode.labelResource()), mode == themeMode) { appearanceViewModel.select(mode) }
                    }
                }
            }

            Column {
                SectionHeader(stringResource(Res.string.profile_section_language))
                SectionCard {
                    AppLanguage.entries.forEachIndexed { index, option ->
                        if (index > 0) RowDivider()
                        ChoiceRow(stringResource(option.labelResource()), option == language) { languageViewModel.select(option) }
                    }
                }
            }

            if (profileState is ProfileViewModel.UiState.Success) {
                SectionCard {
                    ListRow(title = stringResource(Res.string.profile_sign_out), onClick = { confirmSignOut = true })
                }
            }
        }
    }

    if (confirmSignOut) {
        ConfirmDialog(
            title = stringResource(Res.string.profile_sign_out_title),
            message = stringResource(Res.string.profile_sign_out_message),
            confirmLabel = stringResource(Res.string.profile_sign_out),
            onConfirm = {
                confirmSignOut = false
                onSignOut()
            },
            onDismiss = { confirmSignOut = false },
        )
    }
}

@Composable
private fun AccountSection(profile: UserProfile) {
    val name = profile.displayName?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.profile_unnamed)
    Column {
        SectionHeader(stringResource(Res.string.profile_section_account))
        SectionCard {
            ListRow(
                title = name,
                subtitle = profile.email,
                leading = { AvatarCircle(initials = name.take(INITIALS_LENGTH)) },
            )
        }
    }
}

@Composable
private fun ChoiceRow(title: String, selected: Boolean, onSelect: () -> Unit) {
    ListRow(title = title, onClick = onSelect, trailing = { RadioButton(selected = selected, onClick = null) })
}

private const val INITIALS_LENGTH = 1
