package org.structured_barebone.routingcontacts

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
import structuredbarebonecmpproject.shared.generated.resources.common_add
import structuredbarebonecmpproject.shared.generated.resources.common_add_cd
import structuredbarebonecmpproject.shared.generated.resources.common_cancel
import structuredbarebonecmpproject.shared.generated.resources.common_delete_cd
import structuredbarebonecmpproject.shared.generated.resources.common_edit_cd
import structuredbarebonecmpproject.shared.generated.resources.common_save
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_add_title
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_edit_title
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_email_label
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_languages_label
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_name_label
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_priority_field_label
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_priority_label
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_remove_cd
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_surname_label
import structuredbarebonecmpproject.shared.generated.resources.routing_contacts_title
import org.structured_barebone.shared.components.LanguageLabel
import org.structured_barebone.shared.components.MultiLanguageSelectField
import org.structured_barebone.shared.model.RoutingContactDto
import org.structured_barebone.viewmodel.RoutingContactsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutingContactsScreen() {
    val viewModel: RoutingContactsViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<RoutingContactDto?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.routing_contacts_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.common_add_cd))
            }
        },
    ) { padding ->
        when (val state = uiState) {
            is RoutingContactsViewModel.UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is RoutingContactsViewModel.UiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.message)
            }

            is RoutingContactsViewModel.UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.contacts) { contact ->
                        ContactCard(
                            contact = contact,
                            onEdit = { editingContact = contact },
                            onRemove = { viewModel.removeContact(contact.id) },
                            onAvailabilityChange = { viewModel.setAvailability(contact.id, it) },
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ContactDialog(
            title = stringResource(Res.string.routing_contacts_add_title),
            confirmLabel = stringResource(Res.string.common_add),
            onDismiss = { showAddDialog = false },
            onConfirm = { name, surname, email, languages, priority ->
                viewModel.addContact(name, surname, email, languages, true, priority)
                showAddDialog = false
            },
        )
    }

    editingContact?.let { contact ->
        ContactDialog(
            title = stringResource(Res.string.routing_contacts_edit_title),
            confirmLabel = stringResource(Res.string.common_save),
            initialName = contact.name,
            initialSurname = contact.surname,
            initialEmail = contact.email,
            initialLanguages = contact.languagePreferences,
            initialPriority = contact.priority,
            onDismiss = { editingContact = null },
            onConfirm = { name, surname, email, languages, priority ->
                viewModel.updateContact(contact.id, name, surname, email, languages, contact.isAvailable, priority)
                editingContact = null
            },
        )
    }
}

@Composable
private fun ContactCard(
    contact: RoutingContactDto,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    onAvailabilityChange: (Boolean) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${contact.name} ${contact.surname}", style = MaterialTheme.typography.titleMedium)
                Text(text = contact.email, style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    contact.languagePreferences.forEach { code ->
                        LanguageLabel(code = code, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Text(
                    text = stringResource(Res.string.routing_contacts_priority_label, contact.priority.toString()),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Switch(checked = contact.isAvailable, onCheckedChange = onAvailabilityChange)
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(Res.string.common_edit_cd))
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.routing_contacts_remove_cd))
            }
        }
    }
}

@Composable
private fun ContactDialog(
    title: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, List<String>, Int) -> Unit,
    initialName: String = "",
    initialSurname: String = "",
    initialEmail: String = "",
    initialLanguages: List<String> = emptyList(),
    initialPriority: Int = 0,
) {
    var name by remember { mutableStateOf(initialName) }
    var surname by remember { mutableStateOf(initialSurname) }
    var email by remember { mutableStateOf(initialEmail) }
    var languages by remember { mutableStateOf(initialLanguages) }
    var priorityText by remember { mutableStateOf(initialPriority.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(Res.string.routing_contacts_name_label)) })
                OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text(stringResource(Res.string.routing_contacts_surname_label)) })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(Res.string.routing_contacts_email_label)) })
                MultiLanguageSelectField(
                    label = stringResource(Res.string.routing_contacts_languages_label),
                    selectedCodes = languages,
                    onChange = { languages = it },
                )
                OutlinedTextField(
                    value = priorityText,
                    onValueChange = { priorityText = it },
                    label = { Text(stringResource(Res.string.routing_contacts_priority_field_label)) },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(name, surname, email, languages, priorityText.toIntOrNull() ?: 0)
            }) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.common_cancel))
            }
        },
    )
}
