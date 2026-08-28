package com.example.ime.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class ImeThemePalette(
    val id: String,
    val name: String,
    val styleCategory: String, // "Liquid", "Apple", "Material", "Minimalist"
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
            "apple_dark" -> ImeThemePalette(
                id = "apple_dark",
                name = "Apple Cupertino Dark",
                styleCategory = "Apple",
                isDark = true,
                background = Color(0xFF1C1C1E),
                keyBackground = Color(0xFF2C2C2E),
                keyBackgroundPressed = Color(0xFF48484A),
                specialKeyBackground = Color(0xFF222224),
                keyText = Color(0xFFFFFFFF),
                keySubtext = Color(0xFF8E8E93),
                accentPrimary = Color(0xFF0A84FF),
                accentSecondary = Color(0xFF5E5CE6),
                border = Color(0x33FFFFFF),
                candidateBarBg = Color(0x282C2C2E),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFF0A84FF), Color(0xFF5E5CE6)))
            )
            "apple_light" -> ImeThemePalette(
                id = "apple_light",
                name = "Apple Cupertino Light",
                styleCategory = "Apple",
                isDark = false,
                background = Color(0xFFD1D3D9),
                keyBackground = Color(0xFFFFFFFF),
                keyBackgroundPressed = Color(0xFFB4B7BD),
                specialKeyBackground = Color(0xFFE5E6EA),
                keyText = Color(0xFF000000),
                keySubtext = Color(0xFF6C6C70),
                accentPrimary = Color(0xFF007AFF),
                accentSecondary = Color(0xFF5856D6),
                border = Color(0x33000000),
                candidateBarBg = Color(0x26B4B7BD),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFF007AFF), Color(0xFF5856D6)))
            )
            "material_you" -> ImeThemePalette(
                id = "material_you",
                name = "Google Material You",
                styleCategory = "Material",
                isDark = true,
                background = Color(0xFF1E1F22),
                keyBackground = Color(0xFF2B2D31),
                keyBackgroundPressed = Color(0xFF383A40),
                specialKeyBackground = Color(0xFF222428),
                keyText = Color(0xFFE3E2E6),
                keySubtext = Color(0xFFC4C6D0),
                accentPrimary = Color(0xFFA8C7FA),
                accentSecondary = Color(0xFF7Cacf8),
                border = Color(0x20A8C7FA),
                candidateBarBg = Color(0x242B2D31),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFFA8C7FA), Color(0xFFD3E3FD)))
            )
            "liquid_ice" -> ImeThemePalette(
                id = "liquid_ice",
                name = "Liquid Quartz Light",
                styleCategory = "Liquid",
                isDark = false,
                background = Color(0xFFF2F4F8),
                keyBackground = Color(0xFFFFFFFF),
                keyBackgroundPressed = Color(0xFFDDE2EB),
                specialKeyBackground = Color(0xFFE4E8F0),
                keyText = Color(0xFF181A20),
                keySubtext = Color(0xFF64748B),
                accentPrimary = Color(0xFF6366F1),
                accentSecondary = Color(0xFF8B5CF6),
                border = Color(0x22000000),
                candidateBarBg = Color(0x24DDE2EB),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)))
            )
            "cyber_teal", "cyber_neon" -> ImeThemePalette(
                id = "cyber_teal",
                name = "Liquid Aurora Neon",
                styleCategory = "Liquid",
                isDark = true,
                background = Color(0xFF0A0E14),
                keyBackground = Color(0xFF131A24),
                keyBackgroundPressed = Color(0xFF1E2B3C),
                specialKeyBackground = Color(0xFF0E141D),
                keyText = Color(0xFFF0FDF4),
                keySubtext = Color(0xFF34D399),
                accentPrimary = Color(0xFF00E5FF),
                accentSecondary = Color(0xFF10B981),
                border = Color(0x3300E5FF),
                candidateBarBg = Color(0x33131A24),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF10B981)))
            )
            "midnight_bloom" -> ImeThemePalette(
                id = "midnight_bloom",
                name = "Midnight Velvet",
                styleCategory = "Liquid",
                isDark = true,
                background = Color(0xFF120C18),
                keyBackground = Color(0xFF1F1528),
                keyBackgroundPressed = Color(0xFF322242),
                specialKeyBackground = Color(0xFF170E1F),
                keyText = Color(0xFFFAF5FF),
                keySubtext = Color(0xFFF472B6),
                accentPrimary = Color(0xFFE11D48),
                accentSecondary = Color(0xFFA855F7),
                border = Color(0x33F472B6),
                candidateBarBg = Color(0x331F1528),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFFE11D48), Color(0xFFA855F7)))
            )
            "titanium_dark" -> ImeThemePalette(
                id = "titanium_dark",
                name = "Titanium OLED",
                styleCategory = "Minimalist",
                isDark = true,
                background = Color(0xFF000000),
                keyBackground = Color(0xFF141416),
                keyBackgroundPressed = Color(0xFF26262B),
                specialKeyBackground = Color(0xFF0C0C0E),
                keyText = Color(0xFFFFFFFF),
                keySubtext = Color(0xFFA1A1AA),
                accentPrimary = Color(0xFFE4E4E7),
                accentSecondary = Color(0xFFA1A1AA),
                border = Color(0x28FFFFFF),
                candidateBarBg = Color(0x33141416),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFFE4E4E7), Color(0xFFA1A1AA)))
            )
            "minimalist" -> ImeThemePalette(
                id = "minimalist",
                name = "Pristine Light",
                styleCategory = "Minimalist",
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
            else -> ImeThemePalette( // Default: Liquid Obsidian (No blue tint, true liquid slate)
                id = "dynamic",
                name = "Liquid Obsidian",
                styleCategory = "Liquid",
                isDark = true,
                background = Color(0xFF121316),
                keyBackground = Color(0xFF1D1F26),
                keyBackgroundPressed = Color(0xFF2C2F3A),
                specialKeyBackground = Color(0xFF16171D),
                keyText = Color(0xFFF9FAFB),
                keySubtext = Color(0xFF94A3B8),
                accentPrimary = Color(0xFF8B5CF6),
                accentSecondary = Color(0xFFF59E0B),
                border = Color(0x338B5CF6),
                candidateBarBg = Color(0x291D1F26),
                glowBrush = Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFFF59E0B)))
            )
        }
    }

    val ALL_THEMES: List<ImeThemePalette> = listOf(
        getTheme("dynamic"),
        getTheme("apple_dark"),
        getTheme("apple_light"),
        getTheme("material_you"),
        getTheme("liquid_ice"),
        getTheme("cyber_teal"),
        getTheme("midnight_bloom"),
        getTheme("titanium_dark"),
        getTheme("minimalist")
    )
}
