package org.structured_barebone.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class LayoutSizeTest {
    @Test
    fun widths_map_to_layout_classes() {
        assertEquals(LayoutSize.Compact, layoutSizeFor(360.dp))
        assertEquals(LayoutSize.Compact, layoutSizeFor(Breakpoints.medium - 1.dp))
        assertEquals(LayoutSize.Medium, layoutSizeFor(Breakpoints.medium))
        assertEquals(LayoutSize.Medium, layoutSizeFor(Breakpoints.expanded - 1.dp))
        assertEquals(LayoutSize.Expanded, layoutSizeFor(Breakpoints.expanded))
        assertEquals(LayoutSize.Expanded, layoutSizeFor(1920.dp))
    }
}
