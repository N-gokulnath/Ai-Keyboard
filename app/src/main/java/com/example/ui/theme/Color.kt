package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Indigo / Aurora Palette
val PrimaryIndigo = Color(0xFF3525CD)
val PrimaryIndigoLight = Color(0xFF4F46E5)
val PrimaryIndigoContainer = Color(0xFF4F46E5)
val OnPrimaryIndigoContainer = Color(0xFFDAD7FF)

// Iridescent Cyan / AI Accent
val TertiaryCyan = Color(0xFF00505C)
val TertiaryCyanDim = Color(0xFF2FD9F4)
val TertiaryCyanFixed = Color(0xFFA2EEFF)
val OnTertiaryContainer = Color(0xFF83EAFF)

// Background & Dark Surfaces (Apple Liquid Glass Dark Palette)
val BackgroundDark = Color(0xFF0F111A)
val SurfaceDark = Color(0xFF161824)
val SurfaceContainerDark = Color(0xFF1E2030)
val SurfaceContainerHighDark = Color(0xFF27293D)
val SurfaceContainerHighestDark = Color(0xFF32354D)
val GlassSurfaceDark = Color(0xCC1A1C2B)
val GlassBorderDark = Color(0x33C7C4D8)

// Light Surfaces
val BackgroundLight = Color(0xFFFCF8FF)
val SurfaceLight = Color(0xFFF5F2FF)
val SurfaceContainerLight = Color(0xFFF0ECF9)
val SurfaceContainerHighLight = Color(0xFFEAE6F4)
val SurfaceContainerHighestLight = Color(0xFFE4E1EE)
val GlassSurfaceLight = Color(0xCCFFFFFF)
val GlassBorderLight = Color(0x33464555)

// Neutral Text & Outlines
val TextPrimaryDark = Color(0xFFFCF8FF)
val TextSecondaryDark = Color(0xFF9E9DB0)
val TextTertiaryDark = Color(0xFF6B697D)

val TextPrimaryLight = Color(0xFF1B1B24)
val TextSecondaryLight = Color(0xFF505F76)
val TextTertiaryLight = Color(0xFF777587)

// Special accents
val AccentPink = Color(0xFFEC4899)
val AccentPurple = Color(0xFF8B5CF6)
val AccentGreen = Color(0xFF10B981)
val AccentOrange = Color(0xFFF59E0B)

// Aurora Gradients
val AuroraGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF4F46E5),
        Color(0xFF2FD9F4)
    )
)

val AuroraCardGlow = Brush.radialGradient(
    colors = listOf(
        Color(0x402FD9F4),
        Color(0x204F46E5),
        Color(0x00000000)
    )
)

val GlassBorderBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x664F46E5),
        Color(0x662FD9F4),
        Color(0x22FFFFFF)
    )
)
