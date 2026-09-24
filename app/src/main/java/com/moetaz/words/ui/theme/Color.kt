package com.moetaz.words.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val TealPrimary = Color(0xFF2B7A7A)
val TealSecondary = Color(0xFF4CA6A6)
val TealDark = Color(0xFF206A76)
val TealLight = Color(0xFF56B4B3)

val BackgroundLight = Color(0xFFF3F5F4)
val SurfaceCardLight = Color(0xFFE8ECEB)
val SurfaceCardHighlight = Color(0xFFDDE3E0)

val OnSurfaceDark = Color(0xFF1E292B)
val OnSurfaceVariantText = Color(0xFF4A5558)
val OnSurfaceMuted = Color(0xFF788588)

val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(TealDark, TealSecondary)
)

val HeaderGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF226E7A), Color(0xFF3B9294))
)
