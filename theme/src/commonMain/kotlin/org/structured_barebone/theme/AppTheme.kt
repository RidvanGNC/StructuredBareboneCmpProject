package org.structured_barebone.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.structured_barebone.shared.model.ThemeMode

/** Rounded corners built from the [Radius] tokens. */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(Radius.sm),
    small = RoundedCornerShape(Radius.sm),
    medium = RoundedCornerShape(Radius.md),
    large = RoundedCornerShape(Radius.lg),
    extraLarge = RoundedCornerShape(Radius.lg),
)

/**
 * The app theme: Material 3 colors, typography and shapes, plus the [LocalLayoutSize] of the window.
 * A product supplies its own palette or typography through the parameters; the defaults are neutral.
 */
@Composable
fun AppTheme(
    mode: ThemeMode = ThemeMode.SYSTEM,
    lightColors: ColorScheme = lightColorScheme(),
    darkColors: ColorScheme = darkColorScheme(),
    typography: Typography = Typography(),
    content: @Composable () -> Unit,
) {
    val dark = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (dark) darkColors else lightColors,
        typography = typography,
        shapes = AppShapes,
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                CompositionLocalProvider(LocalLayoutSize provides layoutSizeFor(maxWidth)) {
                    content()
                }
            }
        }
    }
}
