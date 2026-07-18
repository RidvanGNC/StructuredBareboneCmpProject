package org.structured_barebone.cmp_project.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Web arayuzundeki AntD primary rengi (#2f54eb) ile eslesir.
private val PrimaryColor = Color(0xFF2F54EB)
private val PrimaryColorDark = Color(0xFF6E8BFF)

private val LightColors = lightColorScheme(
    primary = PrimaryColor,
    secondary = Color(0xFF597EF7),
)

private val DarkColors = darkColorScheme(
    primary = PrimaryColorDark,
    secondary = Color(0xFF85A5FF),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
