package com.example.ime.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.gemini.GeminiService
import com.example.model.AIActionType
import com.example.model.AiChatMessage
import com.example.model.ToneOption
import com.example.model.WritingProfile
import kotlinx.coroutines.launch

/**
 * Compact Conversational AI Assistant Layout for Aura Keyboard.
 * 
 * Features:
 * - Conversational chat history with user bubbles and AI responses
 * - Preset prompt chips (Draft, Reply, Summarize, Rewrite, Polish, Expand, Shorten)
 * - Focused interactive prompt input bar accepting typing directly from Aura IME keys
 * - Send, Copy, Clear/New Chat, and Insert buttons
 * - One-tap insertion into target host application's active InputConnection
 * - Zero recursive keyboards, 100% stable in-place rendering
 */
@Composable
fun AiStudioKeyboardLayout(
    themePalette: ImeThemePalette,
    initialAction: AIActionType = AIActionType.COMPOSE,
    profile: WritingProfile? = null,
    promptText: String = "",
    onPromptChange: (String) -> Unit = {},
    getCurrentInputText: () -> String = { "" },
    onCommitText: (String) -> Unit,
    onReplaceText: (String, Int) -> Unit = { _, _ -> },
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Conversational state
    val messages = remember {
        mutableStateListOf(
            AiChatMessage(
                role = "model",
                content = "👋 **Aura AI Assist is ready**\nChoose a prompt preset or type a question to draft, reply, rewrite or summarize with Gemini."
            )
        )
    }

    var isGenerating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var copiedMessageId by remember { mutableStateOf<String?>(null) }
    var selectedTone by remember { mutableStateOf(ToneOption.PROFESSIONAL) }

    // Scroll to latest message
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Function to send prompt to Gemini
    fun sendPrompt(customPrompt: String? = null, actionType: AIActionType? = null) {
        val query = (customPrompt ?: promptText).trim()
        if (query.isBlank()) return

        val userMessage = AiChatMessage(
            role = "user",
            content = query
        )
        messages.add(userMessage)
        onPromptChange("") // Clear prompt field after sending
        isGenerating = true
        errorMessage = null

        coroutineScope.launch {
            val result = if (actionType != null) {
                GeminiService.processAiAction(
                    actionType = actionType,
                    input = query,
                    tone = selectedTone,
                    profile = profile
                )
            } else {
                GeminiService.generateContent(
                    prompt = query,
                    systemInstruction = "You are Aura AI Assist, a concise and helpful keyboard writing assistant. Keep answers concise, natural, and directly usable."
                )
            }

            isGenerating = false
            result.onSuccess { responseText ->
                val cleaned = responseText.trim()
                messages.add(
                    AiChatMessage(
                        role = "model",
                        content = cleaned
                    )
                )
            }.onFailure { error ->
                errorMessage = error.localizedMessage ?: "AI generation failed. Please check your connection or try again."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // -------------------------------------------------------------
        // TOP HEADER BAR: Back Button, Title, Grab App Text, New Chat
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Back Button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(themePalette.keyBackgroundPressed)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = themePalette.keyText,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // AI Studio Title & Sparkle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = themePalette.accentPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "AI Assist",
                        style = TextStyle(
                            color = themePalette.keyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            // Right header actions: Grab text from app & New Chat
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Grab App Text chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(themePalette.keyBackgroundPressed)
                        .clickable {
                            val hostText = getCurrentInputText()
                            if (hostText.isNotBlank()) {
                                onPromptChange(hostText)
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Grab App Text",
                        color = themePalette.accentSecondary,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // New / Clear Chat button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(themePalette.keyBackgroundPressed)
                        .clickable {
                            messages.clear()
                            messages.add(
                                AiChatMessage(
                                    role = "model",
                                    content = "✨ **New conversation started.** Ask a question or choose a prompt preset below."
                                )
                            )
                            onPromptChange("")
                            errorMessage = null
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "New Chat",
                        tint = themePalette.keySubtext,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // QUICK ACTION PRESET CHIPS
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val presets = listOf(
                Triple("✍️ Draft", AIActionType.COMPOSE, "Draft a friendly and polite message about: "),
                Triple("🔄 Rewrite", AIActionType.REWRITE, "Rewrite the following to sound more polished and engaging: "),
                Triple("💬 Reply", AIActionType.REPLY, "Draft a thoughtful reply to: "),
                Triple("🪄 Fix Grammar", AIActionType.FIX, "Fix all typos, spelling, and grammar mistakes in: "),
                Triple("📝 Summarize", AIActionType.SUMMARIZE, "Summarize this into clear bullet points: "),
                Triple("🌐 Translate", AIActionType.TRANSLATE, "Translate this text to Spanish: ")
            )

            presets.forEach { (label, actionType, promptPrefix) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(themePalette.keyBackgroundPressed)
                        .border(0.5.dp, themePalette.border.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .clickable {
                            val hostText = getCurrentInputText()
                            if (hostText.isNotBlank()) {
                                sendPrompt(hostText, actionType)
                            } else if (promptText.isNotBlank()) {
                                sendPrompt(promptText, actionType)
                            } else {
                                onPromptChange(promptPrefix)
                            }
                        }
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = label,
                        color = themePalette.keyText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // CONVERSATION HISTORY LIST (User bubbles & AI responses)
        // -------------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(themePalette.background.copy(alpha = 0.5f))
                .border(0.5.dp, themePalette.border.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    if (msg.role == "user") {
                        // User message bubble (Right aligned)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(RoundedCornerShape(12.dp, 12.dp, 2.dp, 12.dp))
                                    .background(themePalette.accentPrimary)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = msg.content,
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    } else {
                        // AI Model response bubble (Left aligned with actions)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 2.dp))
                                .background(themePalette.keyBackground)
                                .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(12.dp, 12.dp, 12.dp, 2.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(bottom = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = themePalette.accentSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Aura AI",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themePalette.accentSecondary
                                )
                            }

                            Text(
                                text = msg.content,
                                color = themePalette.keyText,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )

                            // Action buttons for AI response: INSERT INTO APP & COPY
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Refine Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(themePalette.keyBackgroundPressed)
                                        .clickable {
                                            onPromptChange(msg.content)
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.5.dp)
                                ) {
                                    Text(
                                        text = "Refine",
                                        fontSize = 9.5.sp,
                                        color = themePalette.keyText
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // Copy Button
                                val isCopied = copiedMessageId == msg.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(themePalette.keyBackgroundPressed)
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            clipboard?.setPrimaryClip(ClipData.newPlainText("Aura AI", msg.content))
                                            copiedMessageId = msg.id
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.5.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = if (isCopied) Color(0xFF10B981) else themePalette.keySubtext,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = if (isCopied) "Copied" else "Copy",
                                            fontSize = 9.5.sp,
                                            color = if (isCopied) Color(0xFF10B981) else themePalette.keyText
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // INSERT BUTTON (Directly commits into target application)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(themePalette.accentPrimary)
                                        .clickable {
                                            onCommitText(msg.content)
                                            onClose()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 2.5.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = "Insert into App",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Loading Indicator Bubble
                if (isGenerating) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(themePalette.keyBackground)
                                .border(0.5.dp, themePalette.border.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = themePalette.accentPrimary,
                                strokeWidth = 1.5.dp
                            )
                            Text(
                                text = "Gemini is thinking...",
                                color = themePalette.keySubtext,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }

                // Error Message View with Retry
                if (errorMessage != null) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x22EF4444))
                                .border(0.5.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = errorMessage ?: "Generation error",
                                color = Color(0xFFEF4444),
                                fontSize = 10.5.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Retry",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFEF4444))
                                    .clickable { sendPrompt() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // -------------------------------------------------------------
        // INTERACTIVE PROMPT INPUT FIELD + SEND BUTTON
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(themePalette.keyBackground)
                .border(0.5.dp, themePalette.border.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = themePalette.accentPrimary,
                modifier = Modifier.size(14.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Display current prompt text with cursor
            Text(
                text = if (promptText.isEmpty()) "Ask AI anything or tap a preset..." else promptText,
                style = TextStyle(
                    color = if (promptText.isEmpty()) themePalette.keySubtext.copy(alpha = 0.65f) else themePalette.keyText,
                    fontSize = 11.5.sp,
                    fontWeight = if (promptText.isEmpty()) FontWeight.Normal else FontWeight.Medium
                ),
                modifier = Modifier.weight(1f),
                maxLines = 1
            )

            // Clear prompt icon
            if (promptText.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = themePalette.keySubtext,
                    modifier = Modifier
                        .size(15.dp)
                        .clickable { onPromptChange("") }
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Send prompt button
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (promptText.isNotBlank() && !isGenerating) themePalette.accentPrimary
                        else themePalette.keyBackgroundPressed
                    )
                    .clickable(enabled = promptText.isNotBlank() && !isGenerating) {
                        sendPrompt()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (promptText.isNotBlank() && !isGenerating) Color.White else themePalette.keySubtext,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}
