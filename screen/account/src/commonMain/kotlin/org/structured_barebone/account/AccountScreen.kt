package org.structured_barebone.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.structured_barebone.shared.model.ALL_TENANT_PERMISSIONS
import org.structured_barebone.shared.model.AccountStatusDto
import org.structured_barebone.shared.model.AuditLogEntryDto
import org.structured_barebone.shared.model.TenantInvitationDto
import org.structured_barebone.shared.model.TenantInvitationStatus
import org.structured_barebone.shared.model.TenantPermission
import org.structured_barebone.shared.model.TenantUserDto
import org.structured_barebone.viewmodel.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen() {
    val viewModel: AccountViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Şirket") }) },
    ) { padding ->
        when (val state = uiState) {
            is AccountViewModel.UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is AccountViewModel.UiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.message)
            }

            is AccountViewModel.UiState.Success -> Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Şirket Bilgisi") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Kullanıcılar") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Değişiklikler") })
                }

                when (selectedTab) {
                    0 -> CompanyInfoTab(state.status)
                    1 -> UsersTab(state.users, state.invitations, viewModel)
                    else -> AuditLogTab(state.auditLog)
                }
            }
        }
    }
}

@Composable
private fun CompanyInfoTab(status: AccountStatusDto) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Şirket Adı", style = MaterialTheme.typography.labelMedium)
                Text(status.tenantName ?: "-", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun PermissionCheckboxList(
    selected: List<TenantPermission>,
    onChange: (List<TenantPermission>) -> Unit,
) {
    val allSelected = selected.size == ALL_TENANT_PERMISSIONS.size

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = allSelected,
                onCheckedChange = { checked -> onChange(if (checked) ALL_TENANT_PERMISSIONS else emptyList()) },
            )
            Text("Tümünü Seç")
        }
        ALL_TENANT_PERMISSIONS.forEach { permission ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selected.contains(permission),
                    onCheckedChange = { checked ->
                        onChange(if (checked) selected + permission else selected - permission)
                    },
                )
                Text(permission.name)
            }
        }
    }
}

@Composable
private fun UsersTab(
    users: List<TenantUserDto>,
    invitations: List<TenantInvitationDto>,
    viewModel: AccountViewModel,
) {
    var inviteEmail by remember { mutableStateOf("") }
    var invitePermissions by remember { mutableStateOf<List<TenantPermission>>(emptyList()) }
    var editingUser by remember { mutableStateOf<TenantUserDto?>(null) }
    var editPermissions by remember { mutableStateOf<List<TenantPermission>>(emptyList()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text("Üyeler", style = MaterialTheme.typography.titleMedium)
        }
        items(users) { user ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${user.name} ${user.surname}", style = MaterialTheme.typography.bodyLarge)
                        Text(user.email, style = MaterialTheme.typography.bodySmall)
                        if (user.isAdmin) {
                            Text("Admin", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    if (!user.isAdmin) {
                        TextButton(onClick = {
                            editingUser = user
                            editPermissions = user.permissions
                        }) {
                            Text("Yetkileri Düzenle")
                        }
                    }
                }
            }
        }

        item {
            Text("Davetler", style = MaterialTheme.typography.titleMedium)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = inviteEmail,
                    onValueChange = { inviteEmail = it },
                    label = { Text("Davet edilecek e-posta") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Yetkiler", style = MaterialTheme.typography.labelMedium)
                PermissionCheckboxList(selected = invitePermissions, onChange = { invitePermissions = it })
                Button(onClick = {
                    if (inviteEmail.isNotBlank()) {
                        viewModel.inviteUser(inviteEmail, invitePermissions)
                        inviteEmail = ""
                        invitePermissions = emptyList()
                    }
                }) {
                    Text("Davet Et")
                }
            }
        }
        items(invitations) { invitation ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(invitation.email, style = MaterialTheme.typography.bodyLarge)
                        Text(invitation.status.name, style = MaterialTheme.typography.bodySmall)
                    }
                    if (invitation.status == TenantInvitationStatus.Pending) {
                        TextButton(onClick = { viewModel.revokeInvitation(invitation.id) }) {
                            Text("İptal Et")
                        }
                    }
                }
            }
        }
    }

    editingUser?.let { user ->
        AlertDialog(
            onDismissRequest = { editingUser = null },
            title = { Text("Yetkileri Düzenle") },
            text = { PermissionCheckboxList(selected = editPermissions, onChange = { editPermissions = it }) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateMemberPermissions(user.id, editPermissions)
                    editingUser = null
                }) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingUser = null }) {
                    Text("Vazgeç")
                }
            },
        )
    }
}

@Composable
private fun AuditLogTab(entries: List<AuditLogEntryDto>) {
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(entries) { entry ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${entry.actorEmail} · ${entry.action}", style = MaterialTheme.typography.titleSmall)
                    val details = entry.details
                    if (details != null) {
                        Text(details, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(entry.timestamp, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
