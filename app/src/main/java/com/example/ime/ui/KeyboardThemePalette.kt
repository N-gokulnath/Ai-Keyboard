package com.example.ime.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class ImeThemePalette(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val background: Color,
    val keyBackground: Color,
    val keyBackgroundPressed: Color,
    val specialKeyBackground: Color,
    val keyText: Color,
    val keySubtext: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val border: Color,
    val candidateBarBg: Color,
    val glowBrush: Brush
)

object KeyboardThemePalette {

    fun getTheme(themeId: String): ImeThemePalette {
        return when (themeId) {
            "minimalist" -> ImeThemePalette(
                id = "minimalist",
                name = "Pristine Light",
                isDark = false,
                background = Color(0xFFEAEBED),
                keyBackground = Color(0xFFFFFFFF),
                keyBackgroundPressed = Color(0xFFD3D6DA),
                specialKeyBackground = Color(0xFFDCE0E4),
                keyText = Color(0xFF191C20),
                keySubtext = Color(0xFF5D6470),
                accentPrimary = Color(0xFF4F46E5),
                accentSecondary = Color(0xFF6366F1),
                border = Color(0x2E000000),
                candidateBarBg = Color(0x1FCCD1D8),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFF4F46E5), Color(0xFF6366F1)))
            )
            "warm_sand" -> ImeThemePalette(
                id = "warm_sand",
                name = "Warm Sand Light",
                isDark = false,
                background = Color(0xFFF3EFE6),
                keyBackground = Color(0xFFFFFFFF),
                keyBackgroundPressed = Color(0xFFDFDACF),
                specialKeyBackground = Color(0xFFE7E3D8),
                keyText = Color(0xFF2C2720),
                keySubtext = Color(0xFF7B7267),
                accentPrimary = Color(0xFFD97706),
                accentSecondary = Color(0xFFF59E0B),
                border = Color(0x287B7267),
                candidateBarBg = Color(0x24DFDACF),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFFD97706), Color(0xFFF59E0B)))
            )
            "midnight_bloom" -> ImeThemePalette(
                id = "midnight_bloom",
                name = "Midnight Amethyst",
                isDark = true,
                background = Color(0xFF140D1E),
                keyBackground = Color(0xFF241635),
                keyBackgroundPressed = Color(0xFF3C2356),
                specialKeyBackground = Color(0xFF1B1029),
                keyText = Color(0xFFFFFFFF),
                keySubtext = Color(0xFFF472B6),
                accentPrimary = Color(0xFFE11D48),
                accentSecondary = Color(0xFFA855F7),
                border = Color(0x33F472B6),
                candidateBarBg = Color(0x33241635),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFFE11D48), Color(0xFFA855F7)))
            )
            "cyber_teal" -> ImeThemePalette(
                id = "cyber_teal",
                name = "Emerald Slate",
                isDark = true,
                background = Color(0xFF0C1412),
                keyBackground = Color(0xFF162521),
                keyBackgroundPressed = Color(0xFF223B35),
                specialKeyBackground = Color(0xFF101B18),
                keyText = Color(0xFFF0FDF4),
                keySubtext = Color(0xFF6EE7B7),
                accentPrimary = Color(0xFF10B981),
                accentSecondary = Color(0xFF34D399),
                border = Color(0x3334D399),
                candidateBarBg = Color(0x33162521),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF34D399)))
            )
            "titanium_dark" -> ImeThemePalette(
                id = "titanium_dark",
                name = "Titanium OLED",
                isDark = true,
                background = Color(0xFF000000),
                keyBackground = Color(0xFF1C1C1E),
                keyBackgroundPressed = Color(0xFF3A3A3C),
                specialKeyBackground = Color(0xFF121214),
                keyText = Color(0xFFFFFFFF),
                keySubtext = Color(0xFFA1A1AA),
                accentPrimary = Color(0xFF3B82F6),
                accentSecondary = Color(0xFF60A5FA),
                border = Color(0x29FFFFFF),
                candidateBarBg = Color(0x331C1C1E),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)))
            )
            else -> ImeThemePalette( // "dynamic" - Obsidian Charcoal
                id = "dynamic",
                name = "Obsidian Glass",
                isDark = true,
                background = Color(0xFF10121A),
                keyBackground = Color(0xFF1E212E),
                keyBackgroundPressed = Color(0xFF2E3347),
                specialKeyBackground = Color(0xFF161824),
                keyText = Color(0xFFF8FAFC),
                keySubtext = Color(0xFF94A3B8),
                accentPrimary = Color(0xFF6366F1),
                accentSecondary = Color(0xFFF59E0B),
                border = Color(0x336366F1),
                candidateBarBg = Color(0x331E212E),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFFF59E0B)))
            )
        }
    }

    val ALL_THEMES: List<ImeThemePalette> = listOf(
        getTheme("dynamic"),
        getTheme("minimalist"),
        getTheme("warm_sand"),
        getTheme("midnight_bloom"),
        getTheme("cyber_teal"),
        getTheme("titanium_dark")
    )
}
