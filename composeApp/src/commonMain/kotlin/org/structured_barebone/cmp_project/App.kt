package org.structured_barebone.cmp_project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.structured_barebone.cmp_project.resources.Res
import org.structured_barebone.cmp_project.resources.update_required_message
import org.structured_barebone.cmp_project.resources.update_required_title
import org.structured_barebone.data.client.UpdateGate
import org.structured_barebone.feature.FeatureRegistry
import org.structured_barebone.login.LoginScreen
import org.structured_barebone.navigation.FeatureDestination
import org.structured_barebone.navigation.SetupNavGraph
import org.structured_barebone.shared.model.AppMessages
import org.structured_barebone.theme.AppTheme
import org.structured_barebone.theme.Spacing
import org.structured_barebone.ui.LoadingIndicator
import org.structured_barebone.ui.MessageBanner
import org.structured_barebone.ui.StateMessage
import org.structured_barebone.viewmodel.AppearanceViewModel
import org.structured_barebone.viewmodel.LanguageViewModel
import org.structured_barebone.viewmodel.SessionState
import org.structured_barebone.viewmodel.SessionViewModel

/**
 * The app shell: theme, language, the update gate and the sign-in gate around the navigation.
 *
 * @param featureDestinations screens contributed by compiled-in feature modules; shown only while the
 * feature registry's visibility policy allows them.
 */
@Composable
fun App(featureDestinations: List<FeatureDestination> = emptyList()) {
    val languageViewModel: LanguageViewModel = koinViewModel()
    val appearanceViewModel: AppearanceViewModel = koinViewModel()
    val language by languageViewModel.language.collectAsStateWithLifecycle()
    val themeMode by appearanceViewModel.themeMode.collectAsStateWithLifecycle()
    // Kept outside the language key so the user stays on the same screen when the language changes.
    val navController = rememberNavController()

    // Resources read the process locale while a composition is built: set it first, then rebuild everything
    // below when the language changes so no screen keeps text in the previous language.
    remember(language) { applyAppLocale(language) }
    key(language) {
        AppTheme(mode = themeMode) {
            AppContent(navController, featureDestinations)
        }
    }
}

@Composable
private fun AppContent(
    navController: androidx.navigation.NavHostController,
    featureDestinations: List<FeatureDestination>,
) {
    val sessionViewModel: SessionViewModel = koinViewModel()
    val registry: FeatureRegistry = koinInject()
    val updateGate: UpdateGate = koinInject()
    val messages: AppMessages = koinInject()
    val session by sessionViewModel.state.collectAsStateWithLifecycle()
    val updateRequired by updateGate.required.collectAsStateWithLifecycle()
    val message by messages.current.collectAsStateWithLifecycle()

    LaunchedEffect(registry) { registry.start() }
    LaunchedEffect(session) { if (session == SessionState.SIGNED_IN) registry.signedIn() }

    Column(modifier = Modifier.fillMaxSize()) {
        message?.let {
            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(Spacing.sm)) {
                MessageBanner(it, onDismiss = messages::dismiss)
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            when {
                updateRequired -> StateMessage(
                    title = stringResource(Res.string.update_required_title),
                    message = stringResource(Res.string.update_required_message),
                    actionLabel = null,
                )
                session == SessionState.CHECKING -> LoadingIndicator()
                session == SessionState.SIGNED_OUT -> Box(modifier = Modifier.safeDrawingPadding()) { LoginScreen() }
                else -> SetupNavGraph(
                    navController = navController,
                    featureRegistry = registry,
                    featureDestinations = featureDestinations,
                    onSignOut = sessionViewModel::signOut,
                )
            }
        }
    }
}
