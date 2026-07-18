package org.structured_barebone.mails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.structured_barebone.data.domain.MailFilter
import org.structured_barebone.shared.model.MailDto
import org.structured_barebone.shared.model.MailStatus
import org.structured_barebone.shared.model.MailType
import org.structured_barebone.shared.theme.MailStatusColors
import org.structured_barebone.shared.theme.MailTypeColors
import org.structured_barebone.viewmodel.MailListViewModel

@Composable
fun MailsScreen(onMailClick: (String) -> Unit = {}) {
    val viewModel: MailListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val filter by viewModel.filter.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.sync() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Senkronize Et")
            }
        },
    ) { padding ->
        when (val state = uiState) {
            is MailListViewModel.UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is MailListViewModel.UiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.message)
            }

            is MailListViewModel.UiState.Success -> {
                val customerNameById = state.customers.associate { it.id to it.name }
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        MailGroupFilter(
                            groups = state.mailGroups,
                            selected = filter.mailGroup,
                            onSelect = { group -> viewModel.updateFilter(filter.copy(mailGroup = group)) },
                        )
                    }
                    items(state.mails) { mail ->
                        MailCard(
                            mail = mail,
                            customerName = customerNameById[mail.customerId] ?: "-",
                            onClick = { onMailClick(mail.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MailGroupFilter(
    groups: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected ?: "Tüm Gruplar")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Tüm Gruplar") },
                onClick = {
                    onSelect(null)
                    expanded = false
                },
            )
            groups.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group) },
                    onClick = {
                        onSelect(group)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun MailCard(mail: MailDto, customerName: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(10.dp).background(MailTypeColors.forType(mail.type), CircleShape),
            )
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(text = customerName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${mailTypeLabel(mail.type)} · ${mail.predictedLanguage ?: "-"} / ${mail.confirmedLanguage ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(text = mail.mailGroup, style = MaterialTheme.typography.labelSmall)
            }
            if (mail.hasAttachment) {
                Text(text = "📎", modifier = Modifier.padding(end = 8.dp))
            }
            if (mail.isUrgent) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp)
                        .background(Color(0xFFFF4D4F), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Acil",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            StatusBadge(status = mail.status)
        }
    }
}

@Composable
private fun StatusBadge(status: MailStatus) {
    val label = when (status) {
        MailStatus.NotCompleted -> "?"
        MailStatus.InProgress -> "…"
        MailStatus.Completed -> "✓"
    }
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(MailStatusColors.forStatus(status), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}

private fun mailTypeLabel(type: MailType): String = when (type) {
    MailType.Advertisement -> "Reklam"
    MailType.Spam -> "Spam"
    MailType.InquiryOrOffer -> "Soru ve Teklif"
    MailType.Translation -> "Çeviri"
}
