package org.structured_barebone.mailgroups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import structuredbarebonecmpproject.shared.generated.resources.Res
import structuredbarebonecmpproject.shared.generated.resources.common_add_cd
import structuredbarebonecmpproject.shared.generated.resources.common_cancel
import structuredbarebonecmpproject.shared.generated.resources.common_delete_cd
import structuredbarebonecmpproject.shared.generated.resources.common_edit_cd
import structuredbarebonecmpproject.shared.generated.resources.common_save
import structuredbarebonecmpproject.shared.generated.resources.mail_groups_add_title
import structuredbarebonecmpproject.shared.generated.resources.mail_groups_group_name_label
import structuredbarebonecmpproject.shared.generated.resources.mail_groups_no_note
import structuredbarebonecmpproject.shared.generated.resources.mail_groups_note_label
import structuredbarebonecmpproject.shared.generated.resources.mail_groups_title
import org.structured_barebone.shared.model.ManagedMailGroupDto
import org.structured_barebone.viewmodel.MailGroupsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailGroupsScreen(onBack: () -> Unit = {}) {
    val viewModel: MailGroupsViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingNoteFor by remember { mutableStateOf<ManagedMailGroupDto?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.mail_groups_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.common_add_cd))
            }
        },
    ) { padding ->
        when (val state = uiState) {
            is MailGroupsViewModel.UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is MailGroupsViewModel.UiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.message)
            }

            is MailGroupsViewModel.UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.groups) { group ->
                        MailGroupCard(
                            group = group,
                            onToggleActive = { viewModel.setActive(group.id, it) },
                            onEditNote = { editingNoteFor = group },
                            onDelete = { viewModel.delete(group.id) },
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var mailGroup by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(Res.string.mail_groups_add_title)) },
            text = {
                OutlinedTextField(
                    value = mailGroup,
                    onValueChange = { mailGroup = it },
                    label = { Text(stringResource(Res.string.mail_groups_group_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (mailGroup.isNotBlank()) {
                        viewModel.add(mailGroup.trim())
                        showAddDialog = false
                    }
                }) {
                    Text(stringResource(Res.string.common_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            },
        )
    }

    editingNoteFor?.let { group ->
        var note by remember(group.id) { mutableStateOf(group.note ?: "") }
        AlertDialog(
            onDismissRequest = { editingNoteFor = null },
            title = { Text(group.mailGroup) },
            text = {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(Res.string.mail_groups_note_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setNote(group.id, note.ifBlank { null })
                    editingNoteFor = null
                }) {
                    Text(stringResource(Res.string.common_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingNoteFor = null }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            },
        )
    }
}

@Composable
private fun MailGroupCard(
    group: ManagedMailGroupDto,
    onToggleActive: (Boolean) -> Unit,
    onEditNote: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = group.mailGroup, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = group.note?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.mail_groups_no_note),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Switch(checked = group.isActive, onCheckedChange = onToggleActive)
            IconButton(onClick = onEditNote) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(Res.string.common_edit_cd))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.common_delete_cd))
            }
        }
    }
}
