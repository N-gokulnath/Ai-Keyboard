package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AIProcessingMode
import com.example.model.KeyboardSettings
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.AuroraGlowBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.TertiaryCyanDim
import com.example.ui.theme.TertiaryCyanFixed

enum class SettingsFolder(val label: String, val icon: ImageVector) {
    ALL("All", Icons.Default.Tune),
    APPEARANCE("Appearance", Icons.Default.Palette),
    KEYBOARD("Keyboard", Icons.Default.Keyboard),
    TYPING("Typing", Icons.Default.Spellcheck),
    AI_PRIVACY("AI & Privacy", Icons.Default.Security),
    FEEDBACK("Feedback", Icons.Default.Vibration)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: KeyboardSettings = KeyboardSettings(),
    onUpdateSettings: (KeyboardSettings) -> Unit = {},
    onNavigate: (String) -> Unit
) {
    var currentSettings by remember { mutableStateOf(settings) }
    var selectedFolder by remember { mutableStateOf(SettingsFolder.ALL) }

    // Collapsible folder expanded states
    var isAppearanceExpanded by remember { mutableStateOf(true) }
    var isKeyboardExpanded by remember { mutableStateOf(true) }
    var isTypingExpanded by remember { mutableStateOf(true) }
    var isAiPrivacyExpanded by remember { mutableStateOf(true) }
    var isFeedbackExpanded by remember { mutableStateOf(true) }
    var isStorageExpanded by remember { mutableStateOf(true) }

    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark

    Scaffold(
        bottomBar = {
            AppBottomNavBar(currentRoute = "settings", onNavigate = onNavigate)
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top App Bar
                AppTopBar(
                    title = "Settings",
                    onAvatarClick = { onNavigate("profile") },
                    onActionClick = {}
                )

                // Header Banner
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    hasAuroraGlow = true
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF3525CD), Color(0xFF00505C))
                                    )
                                )
                                .border(1.dp, TertiaryCyanDim.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = TertiaryCyanFixed,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Settings & Preferences",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Personalize layout, intelligence engine, themes, and typing ergonomics.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }

                // Category Folders Horizontal Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsFolder.values().forEach { folder ->
                        val isFolderSelected = selectedFolder == folder
                        val chipBg = if (isFolderSelected) PrimaryIndigoContainer else (if (isDark) Color(0x221E2030) else Color(0x12000000))
                        val chipText = if (isFolderSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(chipBg)
                                .border(
                                    1.dp,
                                    if (isFolderSelected) TertiaryCyanDim.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedFolder = folder }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = folder.icon,
                                contentDescription = folder.label,
                                tint = if (isFolderSelected) Color.White else (if (isDark) TertiaryCyanDim else PrimaryIndigoContainer),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = folder.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = chipText,
                                    fontWeight = if (isFolderSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                // 1. FOLDER: APPEARANCE & THEMES
                if (selectedFolder == SettingsFolder.ALL || selectedFolder == SettingsFolder.APPEARANCE) {
                    SettingsFolderSection(
                        title = "Appearance & Themes",
                        subtitle = "App color mode, keyboard height, and insets",
                        icon = Icons.Default.Palette,
                        isExpanded = isAppearanceExpanded,
                        onToggleExpand = { isAppearanceExpanded = !isAppearanceExpanded }
                    ) {
                        // App Theme Mode (Light / Dark / System)
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "App Theme",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = "Switch between light and dark UI",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 12.sp
                                        )
                                    )
                                }

                                val themeDisplay = when (currentSettings.appThemeMode) {
                                    "light" -> "☀️ Light"
                                    "dark" -> "🌙 Dark"
                                    else -> "📱 System"
                                }
                                Text(
                                    text = themeDisplay,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isDark) TertiaryCyanFixed else PrimaryIndigoContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x10000000))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("dark" to "🌙 Dark", "light" to "☀️ Light", "system" to "📱 System").forEach { (mode, label) ->
                                    val isSelected = currentSettings.appThemeMode == mode
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PrimaryIndigoContainer else Color.Transparent)
                                            .clickable {
                                                currentSettings = currentSettings.copy(appThemeMode = mode)
                                                onUpdateSettings(currentSettings)
                                            }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        SettingsDivider()

                        // Keyboard Theme Quick Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate("themes") }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Keyboard Theme",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "Active: ${currentSettings.selectedThemeId.replace("_", " ").replaceFirstChar { it.uppercase() }}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) TertiaryCyanDim else PrimaryIndigoContainer,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Customize",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isDark) TertiaryCyanFixed else PrimaryIndigoContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = if (isDark) TertiaryCyanFixed else PrimaryIndigoContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        SettingsDivider()

                        // Keyboard Height Scale
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Keyboard Height",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                val heightLabel = when (currentSettings.keyboardHeightScale) {
                                    0.9f -> "Compact (0.9x)"
                                    1.15f -> "Tall (1.15x)"
                                    else -> "Standard (1.0x)"
                                }
                                Text(
                                    text = heightLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isDark) TertiaryCyanFixed else PrimaryIndigoContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x10000000))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(0.9f to "Short", 1.0f to "Normal", 1.15f to "Tall").forEach { (scale, label) ->
                                    val isSelected = (currentSettings.keyboardHeightScale - scale).let { it > -0.05f && it < 0.05f }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PrimaryIndigoContainer else Color.Transparent)
                                            .clickable {
                                                currentSettings = currentSettings.copy(keyboardHeightScale = scale)
                                                onUpdateSettings(currentSettings)
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        SettingsDivider()

                        // Bottom Navigation Inset
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Bottom Inset Spacing",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = "Clearance above Android navigation bar",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                Text(
                                    text = "${currentSettings.bottomInsetPaddingDp} dp",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isDark) TertiaryCyanFixed else PrimaryIndigoContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x10000000))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(16 to "Compact (16dp)", 28 to "Standard (28dp)", 36 to "Gboard (36dp)").forEach { (inset, label) ->
                                    val isSelected = currentSettings.bottomInsetPaddingDp == inset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PrimaryIndigoContainer else Color.Transparent)
                                            .clickable {
                                                currentSettings = currentSettings.copy(bottomInsetPaddingDp = inset)
                                                onUpdateSettings(currentSettings)
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. FOLDER: KEYBOARD LAYOUT & KEYS
                if (selectedFolder == SettingsFolder.ALL || selectedFolder == SettingsFolder.KEYBOARD) {
                    SettingsFolderSection(
                        title = "Keyboard Layout & Keys",
                        subtitle = "Number row, globe key, emoji button, and suggestions",
                        icon = Icons.Default.Keyboard,
                        isExpanded = isKeyboardExpanded,
                        onToggleExpand = { isKeyboardExpanded = !isKeyboardExpanded }
                    ) {
                        SettingsToggleRow(
                            title = "Number Row",
                            description = "Always display numbers 0-9 on top of the keyboard",
                            isChecked = currentSettings.showNumberRow,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(showNumberRow = it)
                                onUpdateSettings(currentSettings)
                            }
                        )

                        SettingsDivider()

                        SettingsToggleRow(
                            title = "Emoji Key",
                            description = "Show dedicated emoji shortcut button in the keyboard toolbar",
                            isChecked = currentSettings.showEmojiKey,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(showEmojiKey = it)
                                onUpdateSettings(currentSettings)
                            }
                        )

                        SettingsDivider()

                        SettingsToggleRow(
                            title = "Language Switch Key (Globe)",
                            description = "Display input method switcher next to spacebar",
                            isChecked = currentSettings.showLanguageSwitchKey,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(showLanguageSwitchKey = it)
                                onUpdateSettings(currentSettings)
                            }
                        )

                        SettingsDivider()

                        SettingsToggleRow(
                            title = "Suggestion Strip",
                            description = "Display candidate words and AI action icons above keys",
                            isChecked = currentSettings.showSuggestions,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(showSuggestions = it)
                                onUpdateSettings(currentSettings)
                            }
                        )
                    }
                }

                // 3. FOLDER: TYPING & TEXT CORRECTION
                if (selectedFolder == SettingsFolder.ALL || selectedFolder == SettingsFolder.TYPING) {
                    SettingsFolderSection(
                        title = "Typing & Text Correction",
                        subtitle = "Autocorrect, capitalization, and punctuation rules",
                        icon = Icons.Default.Spellcheck,
                        isExpanded = isTypingExpanded,
                        onToggleExpand = { isTypingExpanded = !isTypingExpanded }
                    ) {
                        SettingsToggleRow(
                            title = "Auto-capitalization",
                            description = "Capitalize the first letter of each sentence automatically",
                            isChecked = currentSettings.autoCapitalization,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(autoCapitalization = it)
                                onUpdateSettings(currentSettings)
                            }
                        )

                        SettingsDivider()

                        SettingsToggleRow(
                            title = "Auto-correction",
                            description = "Spacebar and punctuation automatically correct misspelled words",
                            isChecked = currentSettings.autoCorrection,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(autoCorrection = it)
                                onUpdateSettings(currentSettings)
                            }
                        )

                        SettingsDivider()

                        SettingsToggleRow(
                            title = "Double-space Period",
                            description = "Double tapping spacebar inserts a period followed by a space",
                            isChecked = currentSettings.doubleSpacePeriod,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(doubleSpacePeriod = it)
                                onUpdateSettings(currentSettings)
                            }
                        )
                    }
                }

                // 4. FOLDER: HAPTICS & SOUND FEEDBACK
                if (selectedFolder == SettingsFolder.ALL || selectedFolder == SettingsFolder.FEEDBACK) {
                    SettingsFolderSection(
                        title = "Feedback & Sound",
                        subtitle = "Keypress tactile vibration and audible feedback",
                        icon = Icons.Default.Vibration,
                        isExpanded = isFeedbackExpanded,
                        onToggleExpand = { isFeedbackExpanded = !isFeedbackExpanded }
                    ) {
                        SettingsToggleRow(
                            title = "Haptic Feedback on Keypress",
                            description = "Vibrate softly on every key tap for tactile feedback",
                            isChecked = currentSettings.hapticFeedback,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(hapticFeedback = it)
                                onUpdateSettings(currentSettings)
                            }
                        )

                        SettingsDivider()

                        SettingsToggleRow(
                            title = "Sound on Keypress",
                            description = "Play subtle mechanical click sound on key touch",
                            isChecked = currentSettings.soundOnKeypress,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(soundOnKeypress = it)
                                onUpdateSettings(currentSettings)
                            }
                        )
                    }
                }

                // 5. FOLDER: AI ENGINE & PRIVACY
                if (selectedFolder == SettingsFolder.ALL || selectedFolder == SettingsFolder.AI_PRIVACY) {
                    SettingsFolderSection(
                        title = "AI Engine & Privacy",
                        subtitle = "Inference location, incognito mode, and sensitive field protection",
                        icon = Icons.Default.Security,
                        isExpanded = isAiPrivacyExpanded,
                        onToggleExpand = { isAiPrivacyExpanded = !isAiPrivacyExpanded }
                    ) {
                        // AI Processing Mode Segmented
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "AI Inference Location",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "Choose where intelligence tasks run:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x10000000))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(
                                    AIProcessingMode.LOCAL_ONLY to "🔒 Local Only",
                                    AIProcessingMode.CLOUD_ASSIST to "⚡ Cloud Assist"
                                ).forEach { (mode, label) ->
                                    val isSelected = currentSettings.processingMode == mode
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PrimaryIndigoContainer else Color.Transparent)
                                            .clickable {
                                                currentSettings = currentSettings.copy(processingMode = mode)
                                                onUpdateSettings(currentSettings)
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        SettingsDivider()

                        SettingsToggleRow(
                            title = "Private / Incognito Mode",
                            description = "Process keystrokes locally without network egress or learning history",
                            isChecked = currentSettings.privateMode,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(privateMode = it)
                                onUpdateSettings(currentSettings)
                            }
                        )

                        SettingsDivider()

                        SettingsToggleRow(
                            title = "Sensitive Field Protection",
                            description = "AI automatically disengages on password, OTP, and payment fields",
                            isChecked = currentSettings.sensitiveProtection,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(sensitiveProtection = it)
                                onUpdateSettings(currentSettings)
                            }
                        )
                    }
                }

                // 6. FOLDER: STORAGE & DATA MANAGEMENT
                if (selectedFolder == SettingsFolder.ALL || selectedFolder == SettingsFolder.AI_PRIVACY) {
                    SettingsFolderSection(
                        title = "Storage & Privacy Policy",
                        subtitle = "Clear cache, audit encrypted storage, and review policies",
                        icon = Icons.Default.Folder,
                        isExpanded = isStorageExpanded,
                        onToggleExpand = { isStorageExpanded = !isStorageExpanded }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showClearHistoryDialog = true },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44BA1A1A)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B))
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Delete AI History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(context, "Encrypted Local Storage: 2.8 MB", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Storage: 2.8 MB", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showPrivacyPolicy = true }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = null,
                                    tint = if (isDark) TertiaryCyanDim else PrimaryIndigoContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Read our Zero-Retention Privacy Architecture",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isDark) TertiaryCyanFixed else PrimaryIndigoContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = {
                Text(
                    text = "Clear AI History?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            text = {
                Text(
                    text = "This will wipe all cached prompt completions, transient compose sessions, and temporary predictions immediately.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearHistoryDialog = false
                        Toast.makeText(context, "AI History Cleared Successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = if (isDark) Color(0xFF1E2030) else Color(0xFFFFFFFF),
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Privacy Policy Bottom Sheet
    if (showPrivacyPolicy) {
        ModalBottomSheet(
            onDismissRequest = { showPrivacyPolicy = false },
            sheetState = sheetState,
            containerColor = if (isDark) Color(0xFF161826) else Color(0xFFFFFFFF),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Privacy & Security Policy",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(onClick = { showPrivacyPolicy = false }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "1. Zero Retention: Passwords, OTP codes, credit cards, and protected input variations are never logged, sent to servers, or used for AI training.\n\n" +
                            "2. In-Place Execution: AI suggestions only read text explicitly passed in active compose sessions.\n\n" +
                            "3. On-Device First: Autocorrect, local dictionary, and baseline text modeling run entirely on your device.\n\n" +
                            "4. Provider Transparency: Cloud AI calls use end-to-end TLS encryption with immediate ephemeral processing.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                )

                Button(
                    onClick = { showPrivacyPolicy = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoContainer)
                ) {
                    Text("I Understand", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SettingsFolderSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    content: @Composable () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark
    val chevronRotation by animateFloatAsState(if (isExpanded) 180f else 0f)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Folder Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryIndigoContainer.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isDark) TertiaryCyanDim else PrimaryIndigoContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(chevronRotation)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsDivider()
                    content()
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        SettingsToggle(
            isChecked = isChecked,
            onToggle = onCheckedChange
        )
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    )
}

@Composable
private fun SettingsToggle(
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val knobOffset by animateDpAsState(if (isChecked) 20.dp else 0.dp)
    val toggleBg by animateColorAsState(if (isChecked) PrimaryIndigoContainer else Color(0x33888888))

    Box(
        modifier = Modifier
            .width(48.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(toggleBg)
            .border(1.dp, if (isChecked) TertiaryCyanDim else Color(0x33888888), CircleShape)
            .clickable { onToggle(!isChecked) }
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
