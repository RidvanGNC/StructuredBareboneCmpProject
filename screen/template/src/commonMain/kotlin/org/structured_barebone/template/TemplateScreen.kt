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
import androidx.compose.material3.FilterChip
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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.AppLanguage
import org.structured_barebone.template.resources.*
import org.structured_barebone.viewmodel.LanguageViewModel
import org.structured_barebone.viewmodel.TemplateViewModel

/** Fixture values shown by the demo; not localized because they are data, not text. */
private object DemoFixture {
    const val WORKSPACE_NAME = "Northstar Studio"
    const val PACKAGE_COUNT = 2
    const val OPEN_WORK = "18"
    const val TEAM_MEMBERS = "7"
    const val USAGE = "64%"
}

/** A visual SaaS fixture, not an active tenant integration. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateScreen() {
    val viewModel: TemplateViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(Res.string.app_title)) }) }) { innerPadding ->
        when (val current = state) {
            TemplateViewModel.UiState.Loading -> LoadingWorkspace(Modifier.padding(innerPadding))
            is TemplateViewModel.UiState.Error -> ErrorWorkspace(current.error, Modifier.padding(innerPadding))
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
        Text(stringResource(Res.string.loading_workspace))
    }
}

@Composable
private fun ErrorWorkspace(error: AppError, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(Res.string.workspace_unavailable), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(error.kind.messageResource()), color = MaterialTheme.colorScheme.error)
        error.traceId?.let { traceId ->
            Text(stringResource(Res.string.error_reference, traceId), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun WorkspaceDashboard(status: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(DemoFixture.WORKSPACE_NAME, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(stringResource(Res.string.workspace_description))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(stringResource(Res.string.chip_multi_tenant_demo)) })
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(Res.string.chip_packages_enabled, DemoFixture.PACKAGE_COUNT)) },
                )
            }
        }

        LanguageSelector()

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(Res.string.card_template_state), style = MaterialTheme.typography.labelLarge)
                Text(status, style = MaterialTheme.typography.titleMedium)
                Text(stringResource(Res.string.card_fixture_notice))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(stringResource(Res.string.metric_open_work), DemoFixture.OPEN_WORK, Modifier.weight(1f))
            MetricCard(stringResource(Res.string.metric_team_members), DemoFixture.TEAM_MEMBERS, Modifier.weight(1f))
            MetricCard(stringResource(Res.string.metric_usage), DemoFixture.USAGE, Modifier.weight(1f))
        }

        SectionCard(stringResource(Res.string.section_enabled_packages)) {
            DetailRow(stringResource(Res.string.package_reporting), stringResource(Res.string.package_reporting_detail))
            HorizontalDivider()
            DetailRow(stringResource(Res.string.package_team), stringResource(Res.string.package_team_detail))
        }

        SectionCard(stringResource(Res.string.section_activity)) {
            DetailRow(stringResource(Res.string.activity_report_added), stringResource(Res.string.activity_report_added_time))
            HorizontalDivider()
            DetailRow(stringResource(Res.string.activity_review_ready), stringResource(Res.string.activity_review_ready_time))
            HorizontalDivider()
            DetailRow(stringResource(Res.string.activity_contract_updated), stringResource(Res.string.activity_contract_updated_time))
        }

        Text(
            stringResource(Res.string.footer_tenant_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Lets the user switch the app language; the choice is persisted and rebuilds the UI. */
@Composable
private fun LanguageSelector() {
    val viewModel: LanguageViewModel = koinViewModel()
    val selected by viewModel.language.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(Res.string.language_title), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppLanguage.entries.forEach { language ->
                FilterChip(
                    selected = language == selected,
                    onClick = { viewModel.select(language) },
                    label = { Text(stringResource(language.labelResource())) },
                )
            }
        }
    }
}

private fun AppLanguage.labelResource(): StringResource = when (this) {
    AppLanguage.SYSTEM -> Res.string.language_system
    AppLanguage.ENGLISH -> Res.string.language_english
    AppLanguage.TURKISH -> Res.string.language_turkish
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
