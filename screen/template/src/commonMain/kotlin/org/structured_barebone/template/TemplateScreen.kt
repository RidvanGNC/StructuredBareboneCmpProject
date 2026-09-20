package org.structured_barebone.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.structured_barebone.viewmodel.TemplateViewModel

/** A visual SaaS fixture, not an active tenant integration. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateScreen() {
    val viewModel: TemplateViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Operations Workspace") }) }) { innerPadding ->
        when (val current = state) {
            TemplateViewModel.UiState.Loading -> LoadingWorkspace(Modifier.padding(innerPadding))
            is TemplateViewModel.UiState.Error -> ErrorWorkspace(current.message, Modifier.padding(innerPadding))
            is TemplateViewModel.UiState.Success -> WorkspaceDashboard(
                status = current.value,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun LoadingWorkspace(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircularProgressIndicator(modifier = Modifier.size(28.dp))
        Text("Preparing the demo workspace…")
    }
}

@Composable
private fun ErrorWorkspace(message: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(24.dp)) {
        Text("The demo workspace is unavailable", style = MaterialTheme.typography.titleMedium)
        Text(message, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun WorkspaceDashboard(status: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Northstar Studio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("A SaaS workspace fixture for the mobile and desktop template.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("Multi-tenant demo") })
                AssistChip(onClick = {}, label = { Text("2 packages enabled") })
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Template state", style = MaterialTheme.typography.labelLarge)
                Text(status, style = MaterialTheme.typography.titleMedium)
                Text("This is local fixture data; no tenant is selected or authorized by this screen.")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Open work", "18", Modifier.weight(1f))
            MetricCard("Team members", "7", Modifier.weight(1f))
            MetricCard("Usage", "64%", Modifier.weight(1f))
        }

        SectionCard("Enabled packages") {
            DetailRow("Reporting", "Overview and export")
            HorizontalDivider()
            DetailRow("Team workspace", "Members and roles")
        }

        SectionCard("Workspace activity") {
            DetailRow("Mina added a report", "10 minutes ago")
            HorizontalDivider()
            DetailRow("Quarterly review is ready", "Today")
            HorizontalDivider()
            DetailRow("API contract updated", "Yesterday")
        }

        Text(
            "To make this tenant-aware, opt into :packages:tenant and connect it to the documented backend contract.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun DetailRow(primary: String, secondary: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(primary, fontWeight = FontWeight.Medium)
        Text(secondary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
