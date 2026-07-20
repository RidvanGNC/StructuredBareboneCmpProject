package org.structured_barebone.cmp_project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.structured_barebone.shared.model.SystemNotificationDto
import org.structured_barebone.viewmodel.NotificationsViewModel
import structuredbarebonecmpproject.shared.generated.resources.Res
import structuredbarebonecmpproject.shared.generated.resources.notifications_failed_request
import structuredbarebonecmpproject.shared.generated.resources.notifications_mark_all_read
import structuredbarebonecmpproject.shared.generated.resources.notifications_mark_as_read
import structuredbarebonecmpproject.shared.generated.resources.notifications_missing_cloud_api_key
import structuredbarebonecmpproject.shared.generated.resources.notifications_no_active_model
import structuredbarebonecmpproject.shared.generated.resources.notifications_title

private val SETTINGS_LINKED_TYPES = setOf("NoActiveModel", "MissingCloudApiKey")
private val ErrorColor = Color(0xFFFF4D4F)
private val WarningColor = Color(0xFFFAAD14)
private val ReadColor = Color(0xFF52C41A)

@Composable
private fun describeNotification(notification: SystemNotificationDto): String = when (notification.type) {
    "NoActiveModel" -> stringResource(Res.string.notifications_no_active_model)
    "MissingCloudApiKey" -> stringResource(Res.string.notifications_missing_cloud_api_key, notification.detail ?: "")
    "FailedRequest" -> stringResource(Res.string.notifications_failed_request, notification.detail ?: "")
    else -> notification.detail ?: ""
}

@Composable
fun NotificationBell(onNavigateToSettings: () -> Unit) {
    val viewModel: NotificationsViewModel = koinInject()
    val notifications by viewModel.notifications.collectAsState()
    val readIds by viewModel.readIds.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    if (notifications.isEmpty()) return

    val unreadCount = notifications.count { it.id !in readIds }
    val hasUnreadError = notifications.any { it.severity == "error" && it.id !in readIds }

    Box {
        IconButton(onClick = { expanded = true }) {
            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge(containerColor = if (hasUnreadError) ErrorColor else WarningColor) {
                            Text(unreadCount.toString())
                        }
                    }
                },
            ) {
                Icon(Icons.Default.Notifications, contentDescription = stringResource(Res.string.notifications_title))
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Column(modifier = Modifier.width(320.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(Res.string.notifications_title), style = MaterialTheme.typography.titleMedium)
                    if (unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text(stringResource(Res.string.notifications_mark_all_read), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                HorizontalDivider()
                LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                    items(notifications) { notification ->
                        NotificationRow(
                            notification = notification,
                            isRead = notification.id in readIds,
                            onClick = {
                                if (notification.type in SETTINGS_LINKED_TYPES) {
                                    expanded = false
                                    onNavigateToSettings()
                                }
                            },
                            onMarkRead = { viewModel.markAsRead(notification.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notification: SystemNotificationDto,
    isRead: Boolean,
    onClick: () -> Unit,
    onMarkRead: () -> Unit,
) {
    val linksToSettings = notification.type in SETTINGS_LINKED_TYPES
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (linksToSettings) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = if (notification.severity == "error") ErrorColor else WarningColor,
            modifier = Modifier.padding(top = 2.dp),
        )
        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text(
                text = describeNotification(notification),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (isRead) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ReadColor)
        } else {
            IconButton(onClick = onMarkRead) {
                Icon(Icons.Default.Check, contentDescription = stringResource(Res.string.notifications_mark_as_read))
            }
        }
    }
}
