package org.structured_barebone.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.structured_barebone.viewmodel.TemplateViewModel

@Composable
fun TemplateScreen() {
    val templateViewModel: TemplateViewModel = koinViewModel()
    val uiState by templateViewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val state = uiState) {
                TemplateViewModel.UiState.Loading -> Text("Loading template data…")
                is TemplateViewModel.UiState.Success -> Text(state.value)
                is TemplateViewModel.UiState.Error -> Text(state.message)
            }
        }
    }
}
