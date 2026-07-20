package org.structured_barebone.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import org.structured_barebone.viewmodel.TemplateViewModel

@Composable
fun TemplateScreen(navigateBack: () -> Unit) {
    val templateViewModel: TemplateViewModel = koinInject()

//    val uiState by templateViewModel.uiState.collectAsState()

    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Template Screen")
        }
    }
}