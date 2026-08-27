package com.example.ime.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.gemini.GeminiService
import com.example.model.AIActionType
import com.example.model.ToneOption
import com.example.model.WritingProfile
import kotlinx.coroutines.launch

@Composable
fun AiStudioKeyboardLayout(
    themePalette: ImeThemePalette,
    initialAction: AIActionType = AIActionType.COMPOSE,
    profile: WritingProfile? = null,
    promptText: String = "",
    onPromptChange: (String) -> Unit = {},
    getCurrentInputText: () -> String = { "" },
    onCommitText: (String) -> Unit,
    onReplaceText: (String, Int) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedAction by remember { mutableStateOf(initialAction) }
    var selectedTone by remember { mutableStateOf(ToneOption.PROFESSIONAL) }
    var selectedLanguage by remember { mutableStateOf("Spanish") }

    var originalFetchedLength by remember { mutableIntStateOf(0) }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var copiedFeedback by remember { mutableStateOf(false) }

    // Pre-populate input when opening Rewrite, Fix, Reply, Translate, or Summarize
    LaunchedEffect(selectedAction) {
        if (selectedAction != AIActionType.COMPOSE && promptText.isBlank()) {
            val currentAppText = getCurrentInputText()
            if (currentAppText.isNotBlank()) {
                onPromptChange(currentAppText)
                originalFetchedLength = currentAppText.length
            }
        }
    }

    fun triggerGeneration() {
        val input = promptText.trim()
        if (input.isEmpty() && selectedAction != AIActionType.COMPOSE) {
            errorMessage = "Please enter text or grab text from app first"
            return
        }

        isGenerating = true
        errorMessage = null
        generatedResult = null

        coroutineScope.launch {
            val result = GeminiService.processAiAction(
                actionType = selectedAction,
                input = input,
                tone = selectedTone,
                profile = profile,
                targetLanguage = selectedLanguage
            )

            isGenerating = false
            result.onSuccess { output ->
                generatedResult = output.trim()
            }.onFailure { error ->
                errorMessage = error.localizedMessage ?: "AI generation failed. Please try again."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // TOP HEADER BAR
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
                        text = "AI Studio",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = themePalette.keyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            // ACTION BUTTONS (Grab app text, Paste, Generate)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Grab text from app
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(themePalette.keyBackgroundPressed)
                        .clickable {
                            val appText = getCurrentInputText()
                            if (appText.isNotBlank()) {
                                onPromptChange(appText)
                                originalFetchedLength = appText.length
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

                // Generate button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isGenerating) themePalette.keyBackgroundPressed
                            else themePalette.accentPrimary
                        )
                        .clickable(enabled = !isGenerating) { triggerGeneration() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = themePalette.accentPrimary,
                            strokeWidth = 1.5.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Generate",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Generate",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // ACTION CHIPS (Compose, Rewrite, Reply, Fix Grammar, Summarize, Translate)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val actions = listOf(
                Pair(AIActionType.COMPOSE, "Draft"),
                Pair(AIActionType.REWRITE, "Rewrite"),
                Pair(AIActionType.REPLY, "Reply"),
                Pair(AIActionType.FIX, "Fix Grammar"),
                Pair(AIActionType.SUMMARIZE, "Summarize"),
                Pair(AIActionType.TRANSLATE, "Translate")
            )

            actions.forEach { (action, label) ->
                val isSelected = selectedAction == action
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) themePalette.accentPrimary else themePalette.keyBackgroundPressed)
                        .border(
                            0.5.dp,
                            if (isSelected) themePalette.accentPrimary else themePalette.border.copy(alpha = 0.3f),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            selectedAction = action
                            generatedResult = null
                            errorMessage = null
                        }
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else themePalette.keyText,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        // TONE CHIPS (if not Translate/Summarize)
        if (selectedAction != AIActionType.TRANSLATE && selectedAction != AIActionType.SUMMARIZE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                ToneOption.entries.forEach { tone ->
                    val isToneSelected = selectedTone == tone
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (isToneSelected) themePalette.accentSecondary.copy(alpha = 0.2f) else Color.Transparent)
                            .border(
                                0.5.dp,
                                if (isToneSelected) themePalette.accentSecondary else themePalette.border.copy(alpha = 0.2f),
                                RoundedCornerShape(5.dp)
                            )
                            .clickable { selectedTone = tone }
                            .padding(horizontal = 5.dp, vertical = 1.5.dp)
                    ) {
                        Text(
                            text = tone.label,
                            color = if (isToneSelected) themePalette.accentSecondary else themePalette.keySubtext,
                            fontSize = 9.sp,
                            fontWeight = if (isToneSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // MAIN CONTENT (Prompt Preview OR Result Card)
        if (generatedResult != null) {
            // RESULT CARD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(themePalette.keyBackground)
                    .border(0.5.dp, themePalette.accentPrimary.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                Text(
                    text = generatedResult ?: "",
                    style = TextStyle(
                        color = themePalette.keyText,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Refine / Edit
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(themePalette.keyBackgroundPressed)
                            .clickable {
                                onPromptChange(generatedResult ?: "")
                                generatedResult = null
                            }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("Refine", fontSize = 10.sp, color = themePalette.keyText)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Copy
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(themePalette.keyBackgroundPressed)
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                clipboard?.setPrimaryClip(ClipData.newPlainText("AI Studio", generatedResult ?: ""))
                                copiedFeedback = true
                            }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(if (copiedFeedback) "Copied!" else "Copy", fontSize = 10.sp, color = themePalette.keyText)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Replace in App
                    if (originalFetchedLength > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(themePalette.accentSecondary)
                                .clickable {
                                    generatedResult?.let { onReplaceText(it, originalFetchedLength) }
                                    onClose()
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text("Replace App Text", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Insert into App
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(themePalette.accentPrimary)
                            .clickable {
                                generatedResult?.let { onCommitText(it) }
                                onClose()
                            }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Insert", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // PROMPT INPUT PREVIEW
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(themePalette.keyBackground)
                    .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Text(
                    text = if (promptText.isEmpty()) "Type prompt on keyboard or tap 'Grab App Text' above..." else promptText,
                    style = TextStyle(
                        color = if (promptText.isEmpty()) themePalette.keySubtext.copy(alpha = 0.6f) else themePalette.keyText,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                )

                if (promptText.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Clear prompt",
                            color = themePalette.keySubtext,
                            fontSize = 9.5.sp,
                            modifier = Modifier.clickable { onPromptChange("") }
                        )
                    }
                }
            }
        }
    }
}
