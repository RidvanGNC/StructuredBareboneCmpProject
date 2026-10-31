package org.structured_barebone.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp

/** Width class of the window: phones are compact, tablets medium, desktop windows expanded. */
enum class LayoutSize { Compact, Medium, Expanded }

fun layoutSizeFor(width: Dp): LayoutSize = when {
    width >= Breakpoints.expanded -> LayoutSize.Expanded
    width >= Breakpoints.medium -> LayoutSize.Medium
    else -> LayoutSize.Compact
}

/** The current [LayoutSize]; provided by [AppTheme]. */
val LocalLayoutSize = staticCompositionLocalOf { LayoutSize.Compact }
