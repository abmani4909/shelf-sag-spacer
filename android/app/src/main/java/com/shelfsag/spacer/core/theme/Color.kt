package com.shelfsag.spacer.core.theme

import androidx.compose.ui.graphics.Color
import com.shelfsag.spacer.domain.model.SagRating

const val PresetName = "WARM_EARTHY"

val Background = Color(0xFFF3EDE4)
val BackgroundDeep = Color(0xFFE8DDC4)
val SurfaceRaised = Color(0xFFFBF7F0)
val SurfaceSunken = Color(0xFFEFE6D9)
val Outline = Color(0xFFDCCFBE)
val Primary = Color(0xFF8A5A44)
val PrimaryDark = Color(0xFF6E4634)
val PrimaryLight = Color(0xFFA6755C)
val Accent = Color(0xFF3D7EA6)
val AccentSoft = Color(0xFF9BC2D8)
val RatingInvisible = Color(0xFF7FA46B)
val RatingNoticeable = Color(0xFFD97B4D)
val RatingSagging = Color(0xFFA8442A)
val TextPrimary = Color(0xFF2B211B)
val TextSecondary = Color(0xFF6B5A4C)
val TextMuted = Color(0xFF9A8878)
val SplashTop = Color(0xFF2B211B)
val SplashMid = Color(0xFF4A3326)
val OnDark = Color(0xFFFBF7F0)
val OnDarkMuted = Color(0xFFDCCFBE)

fun ratingColor(rating: SagRating): Color = when (rating) {
    SagRating.INVISIBLE -> RatingInvisible
    SagRating.NOTICEABLE -> RatingNoticeable
    SagRating.SAGGING -> RatingSagging
}

fun ratingWash(rating: SagRating): List<Color> = when (rating) {
    SagRating.INVISIBLE -> listOf(Color(0xFFEDF2E6), Background)
    SagRating.NOTICEABLE -> listOf(Color(0xFFF7EADF), Background)
    SagRating.SAGGING -> listOf(Color(0xFFF6E2DC), Background)
}
