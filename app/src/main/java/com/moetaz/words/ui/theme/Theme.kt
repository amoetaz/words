package com.moetaz.words.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CustomColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    secondary = TealSecondary,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = OnSurfaceDark,
    surface = SurfaceCardLight,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceCardLight,
    onSurfaceVariant = OnSurfaceVariantText,
    outline = TealSecondary
)

@Composable
fun WordsTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CustomColorScheme,
        typography = Typography,
        content = content
    )
}
