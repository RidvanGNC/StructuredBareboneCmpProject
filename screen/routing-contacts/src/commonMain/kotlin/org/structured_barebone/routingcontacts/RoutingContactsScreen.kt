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
import org.koin.compose.koinInject
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
        topBar = { TopAppBar(title = { Text("Sabit Yönlendirmeler") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
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
            title = "Yönlendirme Ekle",
            confirmLabel = "Ekle",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, surname, email, languages, priority ->
                viewModel.addContact(name, surname, email, languages, true, priority)
                showAddDialog = false
            },
        )
    }

    editingContact?.let { contact ->
        ContactDialog(
            title = "Yönlendirmeyi Düzenle",
            confirmLabel = "Kaydet",
            initialName = contact.name,
            initialSurname = contact.surname,
            initialEmail = contact.email,
            initialLanguages = contact.languagePreferences.joinToString(", "),
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
                Text(
                    text = contact.languagePreferences.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(text = "Öncelik: ${contact.priority}", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = contact.isAvailable, onCheckedChange = onAvailabilityChange)
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Düzenle")
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Kaldır")
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
    initialLanguages: String = "",
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
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Ad") })
                OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Soyad") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-posta") })
                OutlinedTextField(
                    value = languages,
                    onValueChange = { languages = it },
                    label = { Text("Diller (virgülle ayırın: tr, en)") },
                )
                OutlinedTextField(
                    value = priorityText,
                    onValueChange = { priorityText = it },
                    label = { Text("Öncelik (düşük sayı önce tercih edilir)") },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val languageList = languages.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                onConfirm(name, surname, email, languageList, priorityText.toIntOrNull() ?: 0)
            }) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        },
    )
}
