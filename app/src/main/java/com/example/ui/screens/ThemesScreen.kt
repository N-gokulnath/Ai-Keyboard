package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemeItem
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.AuroraGlowBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.TertiaryCyanDim
import com.example.ui.theme.TertiaryCyanFixed

@Composable
fun ThemesScreen(
    currentThemeId: String = "dynamic",
    onSelectTheme: (String) -> Unit = {},
    onNavigate: (String) -> Unit
) {
    var selectedThemeId by remember { mutableStateOf(currentThemeId) }
    var showCustomThemeDialog by remember { mutableStateOf(false) }

    val themesList = remember {
        listOf(
            ThemeItem("dynamic", "Obsidian Glass", "Sophisticated graphite glass with amber and indigo accents", 0xFF6366F1, 0xFF10121A, 0xFFF59E0B),
            ThemeItem("minimalist", "Pristine Light", "Crisp porcelain and charcoal keys with clean light styling", 0xFF4F46E5, 0xFFEAEBED, 0xFF6366F1, false),
            ThemeItem("warm_sand", "Warm Sand Light", "Natural warm linen, cream, and espresso accents", 0xFFD97706, 0xFFF3EFE6, 0xFFF59E0B, false),
            ThemeItem("midnight_bloom", "Midnight Amethyst", "Rich imperial violet with rose gold highlights", 0xFFE11D48, 0xFF140D1E, 0xFFA855F7),
            ThemeItem("cyber_teal", "Emerald Slate", "Carbon graphite with vibrant emerald and mint accents", 0xFF10B981, 0xFF0C1412, 0xFF34D399),
            ThemeItem("titanium_dark", "Titanium OLED", "Deep pure OLED black with ice blue highlights", 0xFF3B82F6, 0xFF000000, 0xFF60A5FA)
        )
    }

    Scaffold(
        bottomBar = {
            AppBottomNavBar(currentRoute = "themes", onNavigate = onNavigate)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AuroraGlowBackground(modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top App Bar
                AppTopBar(
                    title = "Aura",
                    onAvatarClick = { onNavigate("profile") },
                    onActionClick = { onNavigate("settings") }
                )

                // Header
                Column {
                    Text(
                        text = "Themes Gallery",
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    )
                    Text(
                        text = "Personalize your AI keyboard aesthetics and glowing keys.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Create Custom Theme CTA Button
                Button(
                    onClick = { showCustomThemeDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoContainer)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Create Custom Theme",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                    }
                }

                // Grid of Themes
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(themesList, key = { it.id }) { theme ->
                        val isSelected = selectedThemeId == theme.id
                        ThemeCard(
                            theme = theme,
                            isSelected = isSelected,
                            onSelect = {
                                selectedThemeId = theme.id
                                onSelectTheme(theme.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCustomThemeDialog) {
        val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark
        AlertDialog(
            onDismissRequest = { showCustomThemeDialog = false },
            containerColor = if (isDark) Color(0xFF171926) else Color(0xFFFFFFFF),
            shape = RoundedCornerShape(28.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = if (isDark) TertiaryCyanDim else PrimaryIndigoContainer
                    )
                    Text("Customize Colors", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Pick your accent tone to generate an intelligent liquid glass shader theme:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ColorPickerCircle(Color(0xFF4F46E5))
                        ColorPickerCircle(Color(0xFF2FD9F4))
                        ColorPickerCircle(Color(0xFFEC4899))
                        ColorPickerCircle(Color(0xFF10B981))
                        ColorPickerCircle(Color(0xFFF59E0B))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCustomThemeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoContainer)
                ) {
                    Text("Apply Palette", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomThemeDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun ThemeCard(
    theme: ThemeItem,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0x221E2030) else Color(0xCCFFFFFF))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) PrimaryIndigoContainer else (if (isDark) Color(0x26FFFFFF) else Color(0x1F000000)),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(10.dp)
        ) {
            // Simulated Keyboard Theme Canvas inside Card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(theme.surfaceColorHex))
                    .border(1.dp, if (theme.isDark) Color(0x1AFFFFFF) else Color(0x1F000000), RoundedCornerShape(14.dp))
                    .padding(8.dp)
            ) {
                // Glow accent blob
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(theme.accentColorHex).copy(alpha = 0.2f))
                        .align(Alignment.TopEnd)
                )

                // Mini Key Mockup Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        repeat(8) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (theme.isDark) Color(0x33FFFFFF) else Color(0x1F000000))
                            )
                        }
                    }
                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        repeat(7) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (theme.isDark) Color(0x33FFFFFF) else Color(0x1F000000))
                            )
                        }
                    }
                    // Row 3 (Spacebar & Action)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (theme.isDark) Color(0x22FFFFFF) else Color(0x15000000))
                        )
                        Box(
                            modifier = Modifier
                                .weight(5f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(theme.primaryColorHex).copy(alpha = 0.6f))
                        )
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(theme.accentColorHex).copy(alpha = 0.8f))
                        )
                    }
                }
            }

            // Selection Checkmark Badge
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(PrimaryIndigoContainer)
                        .border(1.5.dp, Color.White, CircleShape)
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Text(
            text = theme.name,
            style = MaterialTheme.typography.titleSmall.copy(
                color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun ColorPickerCircle(color: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Color.White, CircleShape)
            .clickable {}
    )
}
