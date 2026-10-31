package org.structured_barebone.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.resources.stringResource
import org.structured_barebone.theme.AppLayout
import org.structured_barebone.theme.Spacing
import org.structured_barebone.ui.resources.Res
import org.structured_barebone.ui.resources.action_retry

/** Title bar of a screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(title = { Text(title) }, modifier = modifier, actions = actions)
}

/**
 * Scrolling body of a screen. Content is centered and limited to [AppLayout.contentMaxWidth] so lines stay
 * readable on wide windows.
 */
@Composable
fun ScreenBody(
    modifier: Modifier = Modifier,
    maxWidth: androidx.compose.ui.unit.Dp = AppLayout.contentMaxWidth,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            content = content,
        )
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/** A centered title and message with an optional action, for errors and empty states. */
@Composable
fun StateMessage(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    actionLabel: String? = stringResource(Res.string.action_retry),
    onAction: (() -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxSize().padding(Spacing.xl), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            if (message != null) {
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            if (onAction != null && actionLabel != null) {
                Button(onClick = onAction) { Text(actionLabel) }
            }
        }
    }
}
