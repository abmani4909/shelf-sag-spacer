package com.shelfsag.spacer.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnDark,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = OnDark,
    secondary = Accent,
    onSecondary = OnDark,
    secondaryContainer = AccentSoft,
    onSecondaryContainer = TextPrimary,
    tertiary = RatingNoticeable,
    onTertiary = OnDark,
    background = Background,
    onBackground = TextPrimary,
    surface = SurfaceRaised,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSunken,
    onSurfaceVariant = TextSecondary,
    outline = Outline,
    outlineVariant = Outline,
    error = RatingSagging,
    onError = OnDark,
    scrim = TextPrimary
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
