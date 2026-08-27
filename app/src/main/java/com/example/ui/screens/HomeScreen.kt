package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardRepository
import com.example.model.AIActionType
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.AuroraGlowBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.TertiaryCyanDim
import com.example.ui.theme.TertiaryCyanFixed

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    onQuickAction: (AIActionType) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenTestKeyboard: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { KeyboardRepository.getInstance(context) }
    val settings by repository.settingsFlow.collectAsState()
    val profile by repository.profileFlow.collectAsState()
    val isEnabled = remember { repository.isImeEnabled() }
    val isSelected = remember { repository.isImeSelected() }
    Scaffold(
        bottomBar = {
            AppBottomNavBar(currentRoute = "home", onNavigate = onNavigate)
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Top App Bar
                AppTopBar(
                    title = "Aura",
                    onAvatarClick = onOpenProfile,
                    onActionClick = { onNavigate("settings") }
                )

                // Greeting Section
                Column {
                    Text(
                        text = "Good morning",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Text(
                        text = "Ready to write.",
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp
                        )
                    )
                }

                // Hero Card: AI Compose
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    hasAuroraGlow = true,
                    onClick = { onNavigate("compose") }
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                        // Decorative Background Sparkle
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TertiaryCyanDim.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(130.dp)
                                .align(Alignment.TopEnd)
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PrimaryIndigoContainer.copy(alpha = 0.2f))
                                    .border(1.dp, PrimaryIndigoContainer.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = if (MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark) TertiaryCyanDim else PrimaryIndigoContainer,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Text(
                                text = "AI Compose",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                text = "Tell your keyboard what you want to say, and let Aura draft, rewrite, or reply instantly in place.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 20.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = { onNavigate("compose") },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoContainer)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Try AI Compose", fontWeight = FontWeight.SemiBold, color = Color.White)
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Action Chips
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "QUICK ACTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionChip(
                            icon = Icons.Default.HistoryEdu,
                            label = "Rewrite",
                            modifier = Modifier.weight(1f),
                            onClick = { onQuickAction(AIActionType.REWRITE) }
                        )
                        QuickActionChip(
                            icon = Icons.Default.Reply,
                            label = "Reply",
                            modifier = Modifier.weight(1f),
                            onClick = { onQuickAction(AIActionType.REPLY) }
                        )
                        QuickActionChip(
                            icon = Icons.Default.Spellcheck,
                            label = "Fix",
                            modifier = Modifier.weight(1f),
                            onClick = { onQuickAction(AIActionType.FIX) }
                        )
                        QuickActionChip(
                            icon = Icons.Default.Translate,
                            label = "Translate",
                            modifier = Modifier.weight(1f),
                            onClick = { onQuickAction(AIActionType.TRANSLATE) }
                        )
                    }
                }

                // Bento Grid: Active Status & Writing Profile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Active Keyboard Status Bento Card
                    GlassCard(
                        modifier = Modifier.weight(1f).height(160.dp),
                        shape = RoundedCornerShape(24.dp),
                        onClick = onOpenTestKeyboard
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Keyboard,
                                    contentDescription = null,
                                    tint = if (MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark) TertiaryCyanDim else PrimaryIndigoContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                // Pulsing Active Indicator Dot
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark) TertiaryCyanDim else PrimaryIndigoContainer)
                                )
                            }

                            Column {
                                Text(
                                    text = if (isEnabled) "Active" else "Setup Needed",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = if (isSelected) "Default Keyboard" else "English (US)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryIndigoContainer.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = PrimaryIndigoContainer,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = settings.selectedThemeId.replace("_", " ").replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Writing Profile Bento Card
                    GlassCard(
                        modifier = Modifier.weight(1f).height(160.dp),
                        shape = RoundedCornerShape(24.dp),
                        onClick = onOpenProfile
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryIndigoContainer.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PrimaryIndigoContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = "Writing Profile",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 20.sp
                                )
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                LinearProgressIndicator(
                                    progress = { 0.75f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = if (MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark) TertiaryCyanDim else PrimaryIndigoContainer,
                                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    strokeCap = StrokeCap.Round
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Natural & clear",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = "75%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark) TertiaryCyanDim else PrimaryIndigoContainer,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Keyboard Interactive Testbed Banner
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    onClick = onOpenTestKeyboard
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PrimaryIndigoContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Interactive Keyboard Testbed",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "Test in-app AI writing & live typing",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = if (MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark) TertiaryCyanDim else PrimaryIndigoContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark) TertiaryCyanDim else PrimaryIndigoContainer,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
        }
    }
}
