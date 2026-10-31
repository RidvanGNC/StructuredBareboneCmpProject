package org.structured_barebone.cmp_project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import org.koin.compose.viewmodel.koinViewModel
import org.structured_barebone.navigation.SetupNavGraph
import org.structured_barebone.navigation.route.Screen
import org.structured_barebone.viewmodel.LanguageViewModel

@Composable
fun App() {
    val languageViewModel: LanguageViewModel = koinViewModel()
    val language by languageViewModel.language.collectAsState()

    // Resources read the process locale while a composition is built: set it first, then rebuild everything
    // below when the language changes so no screen keeps text in the previous language.
    remember(language) { applyAppLocale(language) }
    key(language) {
        MaterialTheme {
            SetupNavGraph(startDestination = Screen.Template)
        }
    }
}
