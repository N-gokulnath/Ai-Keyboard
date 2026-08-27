package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardRepository
import com.example.data.gemini.GeminiService
import com.example.model.AIActionType
import com.example.model.ToneOption
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.AuroraGlowBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.ToneChip
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.TertiaryCyanDim
import com.example.ui.theme.TertiaryCyanFixed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ComposeScreen(
    initialAction: AIActionType = AIActionType.COMPOSE,
    onNavigate: (String) -> Unit,
    onInsertText: (String) -> Unit = {}
) {
    var promptText by remember { mutableStateOf("") }
    var selectedAction by remember { mutableStateOf(initialAction) }
    var selectedTone by remember { mutableStateOf(ToneOption.CONCISE) }
    var isSelectedTextContextActive by remember { mutableStateOf(true) }
    var isProfileContextActive by remember { mutableStateOf(true) }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<String?>(null) }
    var hasInserted by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { KeyboardRepository.getInstance(context) }

    val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark

    Scaffold(
        bottomBar = {
            AppBottomNavBar(currentRoute = "compose", onNavigate = onNavigate)
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
                    title = "AI Compose",
                    onAvatarClick = { onNavigate("profile") },
                    onActionClick = { onNavigate("settings") }
                )

                // Header Title
                Column {
                    Text(
                        text = "AI Compose",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Draft intelligent responses effortlessly in-place.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Action Switcher Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AIActionType.entries.forEach { action ->
                        val isSelected = selectedAction == action
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) PrimaryIndigoContainer else (if (isDark) Color(0x1AFFFFFF) else Color(0x10000000)))
                                .border(1.dp, if (isSelected) TertiaryCyanDim else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                                .clickable {
                                    selectedAction = action
                                    if (promptText.isEmpty()) {
                                        promptText = when(action) {
                                            AIActionType.REWRITE -> "Please make this sound more compelling and direct."
                                            AIActionType.REPLY -> "Agree warmly and suggest Friday at 10am."
                                            AIActionType.FIX -> "Can we schedule a sync for tommorow to review design?"
                                            AIActionType.TRANSLATE -> "Hello, looking forward to working with your team."
                                            AIActionType.SUMMARIZE -> "Project status: backend auth completed, latency 40ms, awaiting API review."
                                            else -> ""
                                        }
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = action.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                // Glassmorphic Input Area with Context Overlays
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Editable Text Area
                        Box(modifier = Modifier.weight(1f)) {
                            if (promptText.isEmpty()) {
                                Text(
                                    text = selectedAction.promptHint,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                            BasicTextField(
                                value = promptText,
                                onValueChange = { promptText = it },
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp
                                ),
                                cursorBrush = SolidColor(if (isDark) TertiaryCyanDim else PrimaryIndigoContainer),
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Context Controls Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ContextChip(
                                icon = Icons.Default.TextSnippet,
                                label = "Selected text",
                                isActive = isSelectedTextContextActive,
                                onClick = { isSelectedTextContextActive = !isSelectedTextContextActive }
                            )
                            ContextChip(
                                icon = Icons.Default.Person,
                                label = "Writing profile",
                                isActive = isProfileContextActive,
                                onClick = { isProfileContextActive = !isProfileContextActive }
                            )
                        }
                    }
                }

                // Adjust Tone Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ADJUST TONE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ToneOption.entries.forEach { tone ->
                            ToneChip(
                                label = tone.label,
                                isSelected = selectedTone == tone,
                                onClick = { selectedTone = tone }
                            )
                        }
                    }
                }

                // Generate CTA Button with Aurora Glow
                Button(
                    onClick = {
                        isGenerating = true
                        hasInserted = false
                        coroutineScope.launch {
                            val userProfile = if (isProfileContextActive) repository.profileFlow.value else null
                            val result = GeminiService.processAiAction(
                                actionType = selectedAction,
                                input = promptText.ifEmpty { "Draft a clear, well-structured message" },
                                tone = selectedTone,
                                profile = userProfile
                            )
                            generatedResult = result.getOrNull() ?: "Generated response ready."
                            isGenerating = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigoContainer)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Reasoning with Aura AI...", fontWeight = FontWeight.SemiBold, color = Color.White)
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Generate",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                // Generated Output Area
                AnimatedVisibility(
                    visible = generatedResult != null,
                    enter = fadeIn() + slideInVertically()
                ) {
                    generatedResult?.let { result ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            hasAuroraGlow = true
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = if (isDark) TertiaryCyanDim else PrimaryIndigoContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "AURA GENERATION (${selectedTone.label})",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isDark) TertiaryCyanFixed else PrimaryIndigoContainer,
                                                letterSpacing = 0.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                isGenerating = true
                                                coroutineScope.launch {
                                                    delay(600)
                                                    isGenerating = false
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Regenerate",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {},
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = result,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        lineHeight = 24.sp
                                    )
                                )

                                Button(
                                    onClick = {
                                        hasInserted = true
                                        onInsertText(result)
                                        onNavigate("keyboard_demo")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (hasInserted) Color(0xFF10B981) else PrimaryIndigoContainer
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = if (hasInserted) Icons.Default.Check else Icons.Default.Send,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (hasInserted) "Inserted into Keyboard!" else "Direct Insert into App",
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ContextChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isActive) PrimaryIndigoContainer.copy(alpha = 0.2f) else (if (isDark) Color(0x1AFFFFFF) else Color(0x10000000)))
            .border(1.dp, if (isActive) (if (isDark) TertiaryCyanDim else PrimaryIndigoContainer) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) (if (isDark) TertiaryCyanDim else PrimaryIndigoContainer) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isActive) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }
    }
}
