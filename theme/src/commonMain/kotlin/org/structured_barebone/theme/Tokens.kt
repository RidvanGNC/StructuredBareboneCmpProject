package org.structured_barebone.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design tokens. This module is the only place where raw `dp` and `sp` numbers may appear; every other module
 * uses these names (checked by `StyleRulesTest`).
 */
object Spacing {
    val none: Dp = 0.dp
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
    val xxxl: Dp = 48.dp
}

object Radius {
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val pill: Dp = 100.dp
}

object IconSize {
    val sm: Dp = 16.dp
    val md: Dp = 24.dp
    val lg: Dp = 32.dp
    val tile: Dp = 40.dp
    val avatar: Dp = 48.dp
}

object LineWidth {
    val hairline: Dp = 1.dp
    val thick: Dp = 2.dp
}

object Elevation {
    val none: Dp = 0.dp
    val low: Dp = 1.dp
    val medium: Dp = 3.dp
    val high: Dp = 6.dp
}

/** Content widths that keep text readable on wide windows. */
object AppLayout {
    val contentMaxWidth: Dp = 720.dp
    val formMaxWidth: Dp = 420.dp
    val minTouchTarget: Dp = 48.dp
}

/** Window widths at which the layout class changes (Material window size classes). */
object Breakpoints {
    val medium: Dp = 600.dp
    val expanded: Dp = 840.dp
}
