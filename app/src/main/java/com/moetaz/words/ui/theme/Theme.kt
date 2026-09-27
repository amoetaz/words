package com.moetaz.words.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2F1),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = TealSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECEFF1),
    onSecondaryContainer = Color(0xFF37474F),
    tertiaryContainer = Color(0xFFF1F8E9),
    onTertiaryContainer = Color(0xFF33691E),
    background = BackgroundLight,
    onBackground = OnSurfaceDark,
    surface = SurfaceCardLight,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceCardLight,
    onSurfaceVariant = OnSurfaceVariantText,
    outline = TealSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = Color(0xFF002020),
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = TealSecondaryDark,
    onSecondary = Color(0xFF002020),
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnSurfaceLight,
    surface = SurfaceCardDark,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = OnSurfaceVariantTextDark,
    outline = TealSecondaryDark
)

@Composable
fun WordsTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    darkTheme: Boolean = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    },
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
