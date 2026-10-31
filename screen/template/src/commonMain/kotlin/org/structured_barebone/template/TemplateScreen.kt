package org.structured_barebone.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.structured_barebone.shared.model.AppLanguage
import org.structured_barebone.template.resources.*
import org.structured_barebone.theme.Spacing
import org.structured_barebone.ui.LoadingIndicator
import org.structured_barebone.ui.RowDivider
import org.structured_barebone.ui.ScreenBody
import org.structured_barebone.ui.ScreenHeader
import org.structured_barebone.ui.SectionCard
import org.structured_barebone.ui.SectionHeader
import org.structured_barebone.ui.ListRow
import org.structured_barebone.ui.StateMessage
import org.structured_barebone.ui.labelResource
import org.structured_barebone.ui.messageResource
import org.structured_barebone.ui.resources.Res as UiRes
import org.structured_barebone.ui.resources.language_title
import org.structured_barebone.ui.resources.message_reference
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
@Composable
fun TemplateScreen() {
    val viewModel: TemplateViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        ScreenHeader(stringResource(Res.string.app_title))
        when (val current = state) {
            TemplateViewModel.UiState.Loading -> LoadingIndicator()
            is TemplateViewModel.UiState.Error -> StateMessage(
                title = stringResource(Res.string.workspace_unavailable),
                message = listOfNotNull(
                    stringResource(current.error.kind.messageResource()),
                    current.error.traceId?.let { stringResource(UiRes.string.message_reference, it) },
                ).joinToString("\n"),
                onAction = viewModel::refresh,
            )
            is TemplateViewModel.UiState.Success -> WorkspaceDashboard(status = current.value)
        }
    }
}

@Composable
private fun WorkspaceDashboard(status: String) {
    ScreenBody {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(DemoFixture.WORKSPACE_NAME, style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(Res.string.workspace_description))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                AssistChip(onClick = {}, label = { Text(stringResource(Res.string.chip_multi_tenant_demo)) })
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(Res.string.chip_packages_enabled, DemoFixture.PACKAGE_COUNT)) },
                )
            }
        }

        LanguageSelector()

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(modifier = Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(stringResource(Res.string.card_template_state), style = MaterialTheme.typography.labelLarge)
                Text(status, style = MaterialTheme.typography.titleMedium)
                Text(stringResource(Res.string.card_fixture_notice))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            MetricCard(stringResource(Res.string.metric_open_work), DemoFixture.OPEN_WORK, Modifier.weight(1f))
            MetricCard(stringResource(Res.string.metric_team_members), DemoFixture.TEAM_MEMBERS, Modifier.weight(1f))
            MetricCard(stringResource(Res.string.metric_usage), DemoFixture.USAGE, Modifier.weight(1f))
        }

        Column {
            SectionHeader(stringResource(Res.string.section_enabled_packages))
            SectionCard {
                ListRow(stringResource(Res.string.package_reporting), subtitle = stringResource(Res.string.package_reporting_detail))
                RowDivider()
                ListRow(stringResource(Res.string.package_team), subtitle = stringResource(Res.string.package_team_detail))
            }
        }

        Column {
            SectionHeader(stringResource(Res.string.section_activity))
            SectionCard {
                ListRow(stringResource(Res.string.activity_report_added), subtitle = stringResource(Res.string.activity_report_added_time))
                RowDivider()
                ListRow(stringResource(Res.string.activity_review_ready), subtitle = stringResource(Res.string.activity_review_ready_time))
                RowDivider()
                ListRow(stringResource(Res.string.activity_contract_updated), subtitle = stringResource(Res.string.activity_contract_updated_time))
            }
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
    val selected by viewModel.language.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(stringResource(UiRes.string.language_title), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
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

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(value, style = MaterialTheme.typography.titleLarge)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}
