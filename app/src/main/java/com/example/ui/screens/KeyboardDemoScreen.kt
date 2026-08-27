package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardRepository
import com.example.ime.engine.AospDictionary
import com.example.ime.engine.KeyAction
import com.example.ime.engine.KeyboardMode
import com.example.ime.engine.ShiftState
import com.example.ime.ui.AuraKeyboardView
import com.example.ime.ui.KeyboardThemePalette
import com.example.model.AIActionType
import com.example.ui.components.GlassCard
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.TertiaryCyanDim
import com.example.ui.theme.TertiaryCyanFixed
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val isSender: Boolean,
    val timestamp: String = "10:42 AM"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardDemoScreen(
    initialText: String = "",
    onBack: () -> Unit,
    onOpenFullCompose: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { KeyboardRepository.getInstance(context) }
    val settings by repository.settingsFlow.collectAsState()
    val themePalette = remember(settings.selectedThemeId) { KeyboardThemePalette.getTheme(settings.selectedThemeId) }
    val dictionary = remember { AospDictionary() }

    var currentInputText by remember { mutableStateOf(if (initialText.isNotEmpty()) initialText else "Yes, I'll be there") }
    var shiftState by remember { mutableStateOf(ShiftState.OFF) }
    var keyboardMode by remember { mutableStateOf(KeyboardMode.ALPHA) }
    var composingWord by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf(listOf("sounds", "great!", "thanks")) }

    var showAiActionsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "Hey! Are we still meeting at 3 PM to review the product specs?", false, "10:30 AM"),
            ChatMessage("2", "Yes, just finishing up the prototype walkthrough!", true, "10:35 AM"),
            ChatMessage("3", "Awesome, can you send over the summary so we can review beforehand?", false, "10:40 AM")
        )
    }

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size - 1)
    }

    LaunchedEffect(composingWord) {
        suggestions = dictionary.getSuggestions(composingWord)
    }

    val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App Bar for Demo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0x1AFFFFFF) else Color(0x10000000))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Sarah Jenkins",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Aura Active • English (US)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDark) TertiaryCyanDim else PrimaryIndigoContainer,
                            fontSize = 11.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryIndigoContainer.copy(alpha = 0.2f))
                        .border(1.dp, if (isDark) Color(0x3383EAFF) else PrimaryIndigoContainer.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenFullCompose)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isDark) TertiaryCyanFixed else PrimaryIndigoContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Hub",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Chat Messages Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg, isDark = isDark)
                }
            }

            // Simulated App Input Field Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xFF141420) else Color(0xFFEEEEF4))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Attach",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )

                    // Glass Input Bubble
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDark) Color(0x1FFFFFFF) else Color(0xFFFFFFFF))
                            .border(1.dp, if (isDark) Color(0x334F46E5) else Color(0x1F000000), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = if (currentInputText.isEmpty()) "Message Sarah..." else currentInputText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (currentInputText.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

            // Real Interactive Aura Keyboard Engine Preview
            AuraKeyboardView(
                themePalette = themePalette,
                shiftState = shiftState,
                keyboardMode = keyboardMode,
                suggestions = suggestions,
                actionInfo = KeyAction(android.view.inputmethod.EditorInfo.IME_ACTION_SEND, "Send"),
                isPrivateMode = settings.privateMode,
                settings = settings,
                onKeyChar = { char ->
                    currentInputText += char
                    if (char.length == 1 && char[0].isLetter()) {
                        composingWord += char
                    } else {
                        composingWord = ""
                    }
                    if (shiftState == ShiftState.SHIFT) {
                        shiftState = ShiftState.OFF
                    }
                },
                onBackspace = {
                    if (currentInputText.isNotEmpty()) {
                        currentInputText = currentInputText.dropLast(1)
                    }
                    if (composingWord.isNotEmpty()) {
                        composingWord = composingWord.dropLast(1)
                    }
                },
                onSpace = {
                    currentInputText += " "
                    composingWord = ""
                },
                onShiftToggle = {
                    shiftState = when (shiftState) {
                        ShiftState.OFF -> ShiftState.SHIFT
                        ShiftState.SHIFT -> ShiftState.CAPS_LOCK
                        ShiftState.CAPS_LOCK -> ShiftState.OFF
                    }
                },
                onModeChange = { newMode ->
                    keyboardMode = newMode
                },
                onActionClick = {
                    if (currentInputText.isNotBlank()) {
                        messages.add(
                            ChatMessage(
                                id = System.currentTimeMillis().toString(),
                                text = currentInputText.trim(),
                                isSender = true,
                                timestamp = "Just now"
                            )
                        )
                        currentInputText = ""
                        composingWord = ""
                    }
                },
                onSuggestionClick = { suggestion ->
                    if (composingWord.isNotEmpty() && currentInputText.endsWith(composingWord)) {
                        currentInputText = currentInputText.dropLast(composingWord.length) + suggestion + " "
                    } else {
                        currentInputText = if (currentInputText.isEmpty() || currentInputText.endsWith(" ")) {
                            currentInputText + suggestion + " "
                        } else {
                            currentInputText + " " + suggestion + " "
                        }
                    }
                    composingWord = ""
                },
                onSwitchIme = {
                    keyboardMode = if (keyboardMode == KeyboardMode.ALPHA) KeyboardMode.SYMBOLS else KeyboardMode.ALPHA
                },
                onAiAction = { action ->
                    showAiActionsSheet = true
                },
                onOpenSettings = {
                    onOpenFullCompose()
                }
            )
        }
    }

    // In-place Quick AI Actions Bottom Sheet
    if (showAiActionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAiActionsSheet = false },
            sheetState = sheetState,
            containerColor = if (isDark) Color(0xFF161826) else Color(0xFFFFFFFF),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isDark) TertiaryCyanDim else PrimaryIndigoContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Aura Quick AI Actions",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    IconButton(onClick = { showAiActionsSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }

                // AI Quick Action Options
                AiQuickActionCard(
                    title = "Make Professional",
                    desc = "Rephrase with an executive, respectful tone",
                    icon = Icons.Default.HistoryEdu,
                    onClick = {
                        currentInputText = "Hi Sarah, I've confirmed our 3:00 PM sync. I will have the updated specifications prepared for review."
                        showAiActionsSheet = false
                    }
                )

                AiQuickActionCard(
                    title = "Make Friendly & Casual",
                    desc = "Conversational, enthusiastic, and warm",
                    icon = Icons.Default.Reply,
                    onClick = {
                        currentInputText = "Hey Sarah! Absolutely, looking forward to meeting at 3! Sending over the deck now."
                        showAiActionsSheet = false
                    }
                )

                AiQuickActionCard(
                    title = "Fix Typos & Grammar",
                    desc = "Clean spelling, punctuation, and wording",
                    icon = Icons.Default.Spellcheck,
                    onClick = {
                        currentInputText = "Yes, I'll be there on time."
                        showAiActionsSheet = false
                    }
                )

                AiQuickActionCard(
                    title = "Open Full AI Studio",
                    desc = "Custom prompts, translate, draft, and summarize",
                    icon = Icons.Default.AutoAwesome,
                    onClick = {
                        showAiActionsSheet = false
                        onOpenFullCompose()
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage, isDark: Boolean = true) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isSender) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (message.isSender) 18.dp else 4.dp,
                        bottomEnd = if (message.isSender) 4.dp else 18.dp
                    )
                )
                .background(
                    if (message.isSender) PrimaryIndigoContainer else (if (isDark) Color(0xFF1E1F2F) else Color(0xFFE2E3ED))
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (message.isSender) Color.White else (if (isDark) Color.White else Color(0xFF1B1B22)),
                    lineHeight = 20.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = message.timestamp,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun AiQuickActionCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.BackgroundDark
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryIndigoContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = if (isDark) TertiaryCyanDim else PrimaryIndigoContainer, modifier = Modifier.size(20.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}
