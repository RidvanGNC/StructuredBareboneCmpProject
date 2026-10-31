package org.structured_barebone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import org.structured_barebone.theme.AppLayout
import org.structured_barebone.theme.IconSize
import org.structured_barebone.theme.Spacing

/** Small caption above a [SectionCard]. */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm),
    )
}

/** A rounded group of [ListRow]s; rows inside are separated by the caller with [RowDivider]. */
@Composable
fun SectionCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(content = content)
    }
}

@Composable
fun RowDivider(modifier: Modifier = Modifier) {
    androidx.compose.material3.HorizontalDivider(
        modifier = modifier.padding(start = Spacing.lg),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

/** A settings-style row: optional leading and trailing content, a title and an optional subtitle. */
@Composable
fun ListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val clickable = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppLayout.minTouchTarget)
            .then(clickable)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        leading?.invoke()
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        trailing?.invoke()
    }
}

/** A rounded square that holds a small icon or glyph. */
@Composable
fun IconTile(
    modifier: Modifier = Modifier,
    container: Color = MaterialTheme.colorScheme.primaryContainer,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(IconSize.tile)
            .background(container, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** A circle that shows up to two initials. */
@Composable
fun AvatarCircle(initials: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(IconSize.avatar)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials.take(MAX_INITIALS).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

private const val MAX_INITIALS = 2
