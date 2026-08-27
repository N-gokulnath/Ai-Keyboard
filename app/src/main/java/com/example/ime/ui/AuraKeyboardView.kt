package com.example.ime.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.view.KeyEvent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.gemini.GeminiService
import com.example.ime.engine.KeyAction
import com.example.ime.engine.KeyboardLayouts
import com.example.ime.engine.KeyboardMode
import com.example.ime.engine.ShiftState
import com.example.model.AIActionType
import com.example.model.KeyboardSettings
import com.example.model.ToneOption
import com.example.model.WritingProfile
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AuraKeyboardView(
    themePalette: ImeThemePalette,
    shiftState: ShiftState,
    keyboardMode: KeyboardMode,
    suggestions: List<String>,
    actionInfo: KeyAction,
    isPrivateMode: Boolean,
    settings: KeyboardSettings = KeyboardSettings(),
    profile: WritingProfile? = null,
    initialAiAction: AIActionType = AIActionType.COMPOSE,
    systemNavInsetDp: Dp = 0.dp,
    getCurrentInputText: () -> String = { "" },
    onKeyChar: (String) -> Unit,
    onBackspace: () -> Unit,
    onSpace: () -> Unit,
    onShiftToggle: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    onActionClick: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onSwitchIme: () -> Unit,
    onAiAction: (AIActionType) -> Unit,
    onCommitAiText: (String) -> Unit = {},
    onReplaceAiText: (String, Int) -> Unit = { _, _ -> },
    onOpenSettings: () -> Unit,
    onThemeChange: (String) -> Unit = {},
    onToggleFloatingMode: () -> Unit = {},
    onFloatingScaleChange: (Float) -> Unit = {},
    onFloatingHeightScaleChange: (Float) -> Unit = {},
    onAddRecentEmoji: (String) -> Unit = {},
    onUpdatePinnedTools: (List<String>) -> Unit = {},
    onCommitRichContent: ((Uri, String, String, String?) -> Boolean)? = null,
    onDpadMove: (Int) -> Unit = {},
    onSelectAll: () -> Unit = {},
    onCopy: () -> Unit = {},
    onCut: () -> Unit = {},
    onPaste: () -> Unit = {},
    onUndo: () -> Unit = {},
    onHeightScaleChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMoreDrawer by remember { mutableStateOf(false) }
    var showSuggestionsBar by remember { mutableStateOf(true) }

    // Search and prompt state buffers for upper feature panels
    var gifSearchQuery by remember { mutableStateOf("") }
    var stickerSearchQuery by remember { mutableStateOf("") }
    var emojiSearchQuery by remember { mutableStateOf("") }
    var aiPromptText by remember { mutableStateOf("") }
    var translationInputText by remember { mutableStateOf("") }

    // Floating drag offsets (persisted & live)
    var dragOffsetX by remember(settings.floatingOffsetX) { mutableFloatStateOf(settings.floatingOffsetX) }
    var dragOffsetY by remember(settings.floatingOffsetY) { mutableFloatStateOf(settings.floatingOffsetY) }

    val effectiveHeightMultiplier = if (settings.isFloatingMode) {
        settings.floatingHeightScale.coerceIn(0.7f, 1.3f)
    } else {
        settings.keyboardHeightScale.coerceIn(0.7f, 1.3f)
    }

    val keyHeight = (45 * effectiveHeightMultiplier).coerceIn(34f, 58f).dp

    // Insets handling
    val composeNavInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val composeSysBarsInset = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    val composeTappableInset = WindowInsets.tappableElement.asPaddingValues().calculateBottomPadding()
    val composeGesturesInset = WindowInsets.mandatorySystemGestures.asPaddingValues().calculateBottomPadding()

    val detectedSystemInset = maxOf(
        systemNavInsetDp,
        composeNavInset,
        composeSysBarsInset,
        composeTappableInset,
        composeGesturesInset
    )

    val effectiveBottomInset = if (settings.isFloatingMode) {
        4.dp
    } else if (detectedSystemInset > 0.dp) {
        maxOf(detectedSystemInset, settings.bottomInsetPaddingDp.dp)
    } else {
        if (settings.bottomInsetPaddingDp > 0) settings.bottomInsetPaddingDp.dp else 16.dp
    }

    // Unified dispatching for typing into active search fields / prompt or host app
    fun handleKeyChar(char: String) {
        when (keyboardMode) {
            KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE -> {
                gifSearchQuery += char
            }
            KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE -> {
                stickerSearchQuery += char
            }
            KeyboardMode.EMOJI, KeyboardMode.EMOJI_SEARCH_MODE -> {
                emojiSearchQuery += char
            }
            KeyboardMode.AI_STUDIO, KeyboardMode.AI_CHAT_MODE -> {
                aiPromptText += char
            }
            KeyboardMode.TRANSLATION -> {
                translationInputText += char
            }
            else -> {
                onKeyChar(char)
            }
        }
    }

    fun handleBackspace() {
        when (keyboardMode) {
            KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE -> {
                if (gifSearchQuery.isNotEmpty()) gifSearchQuery = gifSearchQuery.dropLast(1)
                else onBackspace()
            }
            KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE -> {
                if (stickerSearchQuery.isNotEmpty()) stickerSearchQuery = stickerSearchQuery.dropLast(1)
                else onBackspace()
            }
            KeyboardMode.EMOJI, KeyboardMode.EMOJI_SEARCH_MODE -> {
                if (emojiSearchQuery.isNotEmpty()) emojiSearchQuery = emojiSearchQuery.dropLast(1)
                else onBackspace()
            }
            KeyboardMode.AI_STUDIO, KeyboardMode.AI_CHAT_MODE -> {
                if (aiPromptText.isNotEmpty()) aiPromptText = aiPromptText.dropLast(1)
                else onBackspace()
            }
            KeyboardMode.TRANSLATION -> {
                if (translationInputText.isNotEmpty()) translationInputText = translationInputText.dropLast(1)
                else onBackspace()
            }
            else -> {
                onBackspace()
            }
        }
    }

    fun handleSpace() {
        when (keyboardMode) {
            KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE -> {
                gifSearchQuery += " "
            }
            KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE -> {
                stickerSearchQuery += " "
            }
            KeyboardMode.EMOJI, KeyboardMode.EMOJI_SEARCH_MODE -> {
                emojiSearchQuery += " "
            }
            KeyboardMode.AI_STUDIO, KeyboardMode.AI_CHAT_MODE -> {
                aiPromptText += " "
            }
            KeyboardMode.TRANSLATION -> {
                translationInputText += " "
            }
            else -> {
                onSpace()
            }
        }
    }

    // Floating layer modifier vs Docked layer modifier
    val floatingModifier = if (settings.isFloatingMode) {
        Modifier
            .fillMaxWidth(settings.floatingScale.coerceIn(0.68f, 0.96f))
            .offset { IntOffset(dragOffsetX.roundToInt(), dragOffsetY.roundToInt()) }
            .padding(horizontal = 4.dp, vertical = 4.dp)
    } else {
        Modifier.fillMaxWidth()
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = floatingModifier,
            color = themePalette.background,
            shape = if (settings.isFloatingMode) RoundedCornerShape(22.dp) else RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            shadowElevation = if (settings.isFloatingMode) 16.dp else 0.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = if (settings.isFloatingMode) 1.5.dp else 1.dp,
                color = themePalette.border.copy(alpha = if (settings.isFloatingMode) 0.6f else 0.35f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 3.dp, vertical = 3.dp)
            ) {
                // FLOATING HEADER CONTROLS (Rendered when floating mode is active)
                if (settings.isFloatingMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                            .background(themePalette.keyBackgroundPressed)
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Drag indicator & position handle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    dragOffsetX = (dragOffsetX + dragAmount.x).coerceIn(-400f, 400f)
                                    dragOffsetY = (dragOffsetY + dragAmount.y).coerceIn(-600f, 150f)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenWith,
                                contentDescription = "Move Keyboard",
                                tint = themePalette.accentPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(themePalette.accentPrimary.copy(alpha = 0.7f))
                            )
                        }

                        // Floating Scale Controls & Dock button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Scale down (-)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(themePalette.keyBackground)
                                    .clickable { onFloatingScaleChange((settings.floatingScale - 0.05f).coerceAtLeast(0.68f)) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ZoomOut, contentDescription = "Smaller", tint = themePalette.keyText, modifier = Modifier.size(13.dp))
                            }

                            // Scale up (+)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(themePalette.keyBackground)
                                    .clickable { onFloatingScaleChange((settings.floatingScale + 0.05f).coerceAtMost(0.98f)) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ZoomIn, contentDescription = "Larger", tint = themePalette.keyText, modifier = Modifier.size(13.dp))
                            }

                            // Reset Position
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(themePalette.keyBackground)
                                    .clickable {
                                        dragOffsetX = 0f
                                        dragOffsetY = 0f
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset Position", tint = themePalette.keyText, modifier = Modifier.size(13.dp))
                            }

                            // Dock Back Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(themePalette.accentPrimary)
                                    .clickable { onToggleFloatingMode() }
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(Icons.Default.VerticalAlignBottom, contentDescription = "Dock", tint = Color.White, modifier = Modifier.size(11.dp))
                                    Text("Dock", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // TOP TOOLBAR AREA (Gboard style compact toolbar or feature header)
                val isFeatureMode = when (keyboardMode) {
                    KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE,
                    KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE,
                    KeyboardMode.EMOJI, KeyboardMode.EMOJI_SEARCH_MODE,
                    KeyboardMode.AI_STUDIO, KeyboardMode.AI_CHAT_MODE,
                    KeyboardMode.TOOLBAR_CUSTOMIZATION_MODE,
                    KeyboardMode.CLIPBOARD,
                    KeyboardMode.TRANSLATION,
                    KeyboardMode.WRITING_ASSISTANT,
                    KeyboardMode.TEXT_EDITING,
                    KeyboardMode.THEMES -> true
                    else -> false
                }

                if (!isFeatureMode) {
                    // COMPACT TOOLBAR / SUGGESTIONS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(themePalette.candidateBarBg)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Quick Switcher / Drawer toggle
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (showMoreDrawer) themePalette.accentPrimary else themePalette.keyBackgroundPressed)
                                .clickable { showMoreDrawer = !showMoreDrawer },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (showMoreDrawer) Icons.Default.Close else Icons.Default.GridView,
                                contentDescription = "Tools Drawer",
                                tint = if (showMoreDrawer) Color.White else themePalette.accentPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // PINNED TOOLS ROW OR CANDIDATE SUGGESTIONS
                        if (suggestions.isNotEmpty() && showSuggestionsBar && !showMoreDrawer) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                suggestions.forEach { suggestion ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(themePalette.keyBackground)
                                            .clickable { onSuggestionClick(suggestion) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = suggestion,
                                            style = TextStyle(
                                                color = themePalette.keyText,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            // PINNED TOOLS
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val pinnedIds = if (settings.pinnedToolIds.isEmpty()) KeyboardTools.DEFAULT_PINNED_IDS else settings.pinnedToolIds
                                pinnedIds.forEach { toolId ->
                                    val tool = KeyboardTools.getToolById(toolId)
                                    if (tool != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(themePalette.keyBackground)
                                                .clickable {
                                                    when (tool.id) {
                                                        "floating" -> onToggleFloatingMode()
                                                        "settings" -> onOpenSettings()
                                                        else -> onModeChange(tool.targetMode)
                                                    }
                                                }
                                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = tool.icon,
                                                    contentDescription = tool.title,
                                                    tint = themePalette.accentPrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = tool.shortName,
                                                    style = TextStyle(
                                                        color = themePalette.keyText,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Right actions: More & AI Quick Launch
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            // More Drawer Button
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(themePalette.keyBackgroundPressed)
                                    .clickable { showMoreDrawer = !showMoreDrawer },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "More Tools",
                                    tint = themePalette.keyText,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            // AI Studio Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Brush.horizontalGradient(listOf(themePalette.accentPrimary, themePalette.accentSecondary)))
                                    .clickable { onModeChange(KeyboardMode.AI_CHAT_MODE) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Studio",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "AI",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // MORE TOOLS DRAWER (When expanded)
                if (showMoreDrawer && !isFeatureMode) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(KeyboardTools.ALL_TOOLS, key = { it.id }) { tool ->
                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(themePalette.keyBackground)
                                        .border(0.5.dp, themePalette.border.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            showMoreDrawer = false
                                            when (tool.id) {
                                                "floating" -> onToggleFloatingMode()
                                                "settings" -> onOpenSettings()
                                                else -> onModeChange(tool.targetMode)
                                            }
                                        }
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = tool.icon,
                                            contentDescription = tool.title,
                                            tint = themePalette.accentPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = tool.shortName,
                                            style = TextStyle(
                                                color = themePalette.keyText,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // UPPER FEATURE PANELS (When a mode is active)
                when (keyboardMode) {
                    KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE -> {
                        GifKeyboardLayout(
                            themePalette = themePalette,
                            searchQuery = gifSearchQuery,
                            onQueryChange = { gifSearchQuery = it },
                            onSelectGif = { gif ->
                                onCommitRichContent?.let { commit ->
                                    val uri = Uri.parse(gif.mediaUrl)
                                    val success = commit(uri, "image/gif", gif.title, gif.mediaUrl)
                                    if (!success) onKeyChar(gif.previewEmoji)
                                } ?: onKeyChar(gif.previewEmoji)
                                onModeChange(KeyboardMode.NORMAL_MODE)
                            },
                            onBackToAlpha = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE -> {
                        StickerKeyboardLayout(
                            themePalette = themePalette,
                            searchQuery = stickerSearchQuery,
                            onQueryChange = { stickerSearchQuery = it },
                            onSelectSticker = { sticker ->
                                onCommitRichContent?.let { commit ->
                                    val uri = Uri.parse(sticker.imageUrl.ifEmpty { "https://api.dicebear.com/7.x/bottts/png?seed=${sticker.name}" })
                                    val success = commit(uri, "image/png", sticker.name, sticker.imageUrl)
                                    if (!success) onKeyChar(sticker.emoji)
                                } ?: onKeyChar(sticker.emoji)
                                onModeChange(KeyboardMode.NORMAL_MODE)
                            },
                            onBackToAlpha = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    KeyboardMode.EMOJI, KeyboardMode.EMOJI_SEARCH_MODE -> {
                        EmojiKeyboardPanel(
                            themePalette = themePalette,
                            searchQuery = emojiSearchQuery,
                            recentEmojis = settings.recentEmojis,
                            onQueryChange = { emojiSearchQuery = it },
                            onSelectEmoji = { emoji ->
                                onAddRecentEmoji(emoji)
                                onKeyChar(emoji)
                            },
                            onBackToAlpha = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    KeyboardMode.AI_STUDIO, KeyboardMode.AI_CHAT_MODE -> {
                        AiStudioKeyboardLayout(
                            themePalette = themePalette,
                            initialAction = initialAiAction,
                            profile = profile,
                            promptText = aiPromptText,
                            onPromptChange = { aiPromptText = it },
                            getCurrentInputText = getCurrentInputText,
                            onCommitText = { text ->
                                onCommitAiText(text)
                                onModeChange(KeyboardMode.NORMAL_MODE)
                            },
                            onReplaceText = { text, len ->
                                onReplaceAiText(text, len)
                                onModeChange(KeyboardMode.NORMAL_MODE)
                            },
                            onClose = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    KeyboardMode.TOOLBAR_CUSTOMIZATION_MODE -> {
                        ToolbarCustomizerLayout(
                            themePalette = themePalette,
                            pinnedToolIds = if (settings.pinnedToolIds.isEmpty()) KeyboardTools.DEFAULT_PINNED_IDS else settings.pinnedToolIds,
                            onUpdatePinnedTools = onUpdatePinnedTools,
                            onDone = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    KeyboardMode.CLIPBOARD -> {
                        ClipboardPanel(
                            themePalette = themePalette,
                            onSelectClip = { clip ->
                                onKeyChar(clip)
                                onModeChange(KeyboardMode.NORMAL_MODE)
                            },
                            onBackToAlpha = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    KeyboardMode.TRANSLATION -> {
                        TranslationPanel(
                            themePalette = themePalette,
                            inputText = translationInputText,
                            onInputTextChange = { translationInputText = it },
                            onInsertTranslation = { translated ->
                                onKeyChar(translated)
                                onModeChange(KeyboardMode.NORMAL_MODE)
                            },
                            onBackToAlpha = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    KeyboardMode.TEXT_EDITING -> {
                        TextEditingPanel(
                            themePalette = themePalette,
                            onDpadMove = onDpadMove,
                            onSelectAll = onSelectAll,
                            onCopy = onCopy,
                            onCut = onCut,
                            onPaste = onPaste,
                            onUndo = onUndo,
                            onBackToAlpha = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    KeyboardMode.THEMES -> {
                        ThemesPanel(
                            themePalette = themePalette,
                            selectedThemeId = settings.selectedThemeId,
                            onSelectTheme = { themeId ->
                                onThemeChange(themeId)
                            },
                            onBackToAlpha = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    KeyboardMode.WRITING_ASSISTANT -> {
                        WritingAssistantPanel(
                            themePalette = themePalette,
                            getCurrentInputText = getCurrentInputText,
                            onReplaceText = { newText, len ->
                                onReplaceAiText(newText, len)
                                onModeChange(KeyboardMode.NORMAL_MODE)
                            },
                            onBackToAlpha = { onModeChange(KeyboardMode.NORMAL_MODE) }
                        )
                    }

                    else -> {
                        // Normal mode or standard typing
                    }
                }

                // BOTTOM UNIFIED KEYBOARD ENGINE
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // NUMBER ROW (Optional)
                    if (settings.showNumberRow && keyboardMode != KeyboardMode.SYMBOLS && keyboardMode != KeyboardMode.MORE_SYMBOLS) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            KeyboardLayouts.NUMBERS_ROW.forEach { num ->
                                ImeKey(
                                    label = num,
                                    themePalette = themePalette,
                                    height = (keyHeight * 0.75f),
                                    modifier = Modifier.weight(1f),
                                    onClick = { handleKeyChar(num) }
                                )
                            }
                        }
                    }

                    // STANDARD QWERTY ROWS OR SYMBOL ROWS
                    when (keyboardMode) {
                        KeyboardMode.SYMBOLS -> {
                            // SYMBOLS ROW 1
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                KeyboardLayouts.SYMBOLS_ROW_1.forEach { char ->
                                    ImeKey(label = char, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { handleKeyChar(char) })
                                }
                            }
                            // SYMBOLS ROW 2
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                KeyboardLayouts.SYMBOLS_ROW_2.forEach { char ->
                                    ImeKey(label = char, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { handleKeyChar(char) })
                                }
                            }
                            // SYMBOLS ROW 3
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                                ImeSpecialKey(
                                    label = "=\\<",
                                    themePalette = themePalette,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1.3f),
                                    onClick = { onModeChange(KeyboardMode.MORE_SYMBOLS) }
                                )
                                KeyboardLayouts.SYMBOLS_ROW_3.forEach { char ->
                                    ImeKey(label = char, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { handleKeyChar(char) })
                                }
                                ImeSpecialKey(
                                    icon = Icons.AutoMirrored.Filled.Backspace,
                                    themePalette = themePalette,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1.3f),
                                    onClick = { handleBackspace() }
                                )
                            }
                        }

                        KeyboardMode.MORE_SYMBOLS -> {
                            val moreRow1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
                            val moreRow2 = listOf("£", "¢", "€", "¥", "^", "°", "=", "{", "}", "\\")
                            val moreRow3 = listOf("%", "©", "®", "™", "✓", "[", "]")

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                moreRow1.forEach { char ->
                                    ImeKey(label = char, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { handleKeyChar(char) })
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                moreRow2.forEach { char ->
                                    ImeKey(label = char, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { handleKeyChar(char) })
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                                ImeSpecialKey(
                                    label = "?123",
                                    themePalette = themePalette,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1.3f),
                                    onClick = { onModeChange(KeyboardMode.SYMBOLS) }
                                )
                                moreRow3.forEach { char ->
                                    ImeKey(label = char, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { handleKeyChar(char) })
                                }
                                ImeSpecialKey(
                                    icon = Icons.AutoMirrored.Filled.Backspace,
                                    themePalette = themePalette,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1.3f),
                                    onClick = { handleBackspace() }
                                )
                            }
                        }

                        else -> {
                            // PRIMARY QWERTY ENGINE
                            // ROW 1
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                KeyboardLayouts.QWERTY_ROW_1.forEach { char ->
                                    val displayChar = if (shiftState != ShiftState.OFF) char.uppercase() else char
                                    ImeKey(label = displayChar, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { handleKeyChar(displayChar) })
                                }
                            }

                            // ROW 2
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Spacer(modifier = Modifier.weight(0.5f))
                                KeyboardLayouts.QWERTY_ROW_2.forEach { char ->
                                    val displayChar = if (shiftState != ShiftState.OFF) char.uppercase() else char
                                    ImeKey(label = displayChar, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { handleKeyChar(displayChar) })
                                }
                                Spacer(modifier = Modifier.weight(0.5f))
                            }

                            // ROW 3: Shift, Keys, Backspace
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                                ImeSpecialKey(
                                    icon = Icons.Default.KeyboardCapslock,
                                    themePalette = themePalette,
                                    height = keyHeight,
                                    isActive = shiftState != ShiftState.OFF,
                                    modifier = Modifier.weight(1.3f),
                                    onClick = { onShiftToggle() }
                                )
                                KeyboardLayouts.QWERTY_ROW_3.forEach { char ->
                                    val displayChar = if (shiftState != ShiftState.OFF) char.uppercase() else char
                                    ImeKey(label = displayChar, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { handleKeyChar(displayChar) })
                                }
                                ImeSpecialKey(
                                    icon = Icons.AutoMirrored.Filled.Backspace,
                                    themePalette = themePalette,
                                    height = keyHeight,
                                    modifier = Modifier.weight(1.3f),
                                    onClick = { handleBackspace() }
                                )
                            }
                        }
                    }

                    // BOTTOM ROW (Mode switch, Language, Spacebar, Dot, Action Key)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ?123 / ABC TOGGLE
                        val isSymbols = keyboardMode == KeyboardMode.SYMBOLS || keyboardMode == KeyboardMode.MORE_SYMBOLS
                        ImeSpecialKey(
                            label = if (isSymbols) "ABC" else "?123",
                            themePalette = themePalette,
                            height = keyHeight,
                            modifier = Modifier.weight(1.3f),
                            onClick = {
                                if (isSymbols) onModeChange(KeyboardMode.NORMAL_MODE)
                                else onModeChange(KeyboardMode.SYMBOLS)
                            }
                        )

                        // EMOJI / LANGUAGE KEY (if enabled)
                        if (settings.showEmojiKey) {
                            ImeSpecialKey(
                                icon = Icons.Default.SentimentSatisfied,
                                themePalette = themePalette,
                                height = keyHeight,
                                modifier = Modifier.weight(0.9f),
                                onClick = { onModeChange(KeyboardMode.EMOJI_SEARCH_MODE) }
                            )
                        }

                        // COMMA KEY
                        ImeKey(
                            label = ",",
                            themePalette = themePalette,
                            height = keyHeight,
                            modifier = Modifier.weight(0.9f),
                            onClick = { handleKeyChar(",") }
                        )

                        // SPACEBAR
                        Box(
                            modifier = Modifier
                                .weight(3.5f)
                                .height(keyHeight)
                                .clip(RoundedCornerShape(6.dp))
                                .background(themePalette.keyBackground)
                                .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .clickable { handleSpace() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aura",
                                style = TextStyle(
                                    color = themePalette.keySubtext.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        // PERIOD KEY
                        ImeKey(
                            label = ".",
                            themePalette = themePalette,
                            height = keyHeight,
                            modifier = Modifier.weight(0.9f),
                            onClick = { handleKeyChar(".") }
                        )

                        // ACTION / ENTER KEY
                        val actionLabel = when (actionInfo.actionId) {
                            android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH -> "Search"
                            android.view.inputmethod.EditorInfo.IME_ACTION_SEND -> "Send"
                            android.view.inputmethod.EditorInfo.IME_ACTION_GO -> "Go"
                            android.view.inputmethod.EditorInfo.IME_ACTION_NEXT -> "Next"
                            android.view.inputmethod.EditorInfo.IME_ACTION_DONE -> "Done"
                            else -> "Enter"
                        }
                        val actionIcon: ImageVector = when (actionInfo.actionId) {
                            android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH -> Icons.Default.Search
                            android.view.inputmethod.EditorInfo.IME_ACTION_SEND -> Icons.Default.Send
                            android.view.inputmethod.EditorInfo.IME_ACTION_DONE -> Icons.Default.Done
                            else -> Icons.Default.KeyboardReturn
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.4f)
                                .height(keyHeight)
                                .clip(RoundedCornerShape(6.dp))
                                .background(themePalette.accentPrimary)
                                .clickable { onActionClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = actionIcon,
                                    contentDescription = actionLabel,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                // BOTTOM FLOATING RESIZE & DRAG PILL (When in floating mode)
                if (settings.isFloatingMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(4.5.dp)
                                .clip(RoundedCornerShape(2.5.dp))
                                .background(themePalette.keySubtext.copy(alpha = 0.5f))
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        dragOffsetX = (dragOffsetX + dragAmount.x).coerceIn(-400f, 400f)
                                        dragOffsetY = (dragOffsetY + dragAmount.y).coerceIn(-600f, 150f)
                                    }
                                }
                        )
                    }
                }

                // BOTTOM SYSTEM INSET SPACER (Docked mode only)
                if (!settings.isFloatingMode && effectiveBottomInset > 0.dp) {
                    Spacer(modifier = Modifier.height(effectiveBottomInset))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPOSABLES: Keys, Emoji Panel, Clipboard, Translate, etc.
// -------------------------------------------------------------

@Composable
fun ImeKey(
    label: String,
    themePalette: ImeThemePalette,
    height: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .background(themePalette.keyBackground)
            .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
        .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = themePalette.keyText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun ImeSpecialKey(
    label: String? = null,
    icon: ImageVector? = null,
    themePalette: ImeThemePalette,
    height: Dp,
    isActive: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) themePalette.accentPrimary else themePalette.specialKeyBackground)
            .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label ?: "Key",
                tint = if (isActive) Color.White else themePalette.keyText,
                modifier = Modifier.size(17.dp)
            )
        } else if (label != null) {
            Text(
                text = label,
                style = TextStyle(
                    color = if (isActive) Color.White else themePalette.keyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
fun EmojiKeyboardPanel(
    themePalette: ImeThemePalette,
    searchQuery: String = "",
    recentEmojis: List<String> = emptyList(),
    onQueryChange: (String) -> Unit = {},
    onSelectEmoji: (String) -> Unit,
    onBackToAlpha: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Smileys") }

    val allEmojis = remember {
        mapOf(
            "Smileys" to listOf("😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥹", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸", "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😮‍💨", "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🫣", "🤗", "🫡", "🤔", "🫣", "🤭", "🤫", "🤥", "😶", "😶‍🌫️", "😐", "😑", "😬", "🫨", "🫠", "🤤", "🥱", "😴", "😷", "🤒", "🤕", "🤢", "🤮", "🤧", "😇", "🤠", "🥳", "🤡", "💩", "👻", "💀", "👽", "👾", "🤖"),
            "Gestures" to listOf("👍", "👎", "👊", "✊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂", "🦻", "👃", "🫀", "🫁", "🧠", "👀", "👁️", "👅", "👄", "🫦", "👶", "👧", "🧒", "👦", "👩", "🧑", "👨", "👩‍🦱", "👨‍🦱", "👩‍🦰", "👨‍🦰", "👱‍♀️", "👱‍♂️", "👩‍🦳", "👨‍🦳", "👩‍🦲", "👨‍🦲", "👵", "🧓", "👴", "👲", "👳‍♀️", "👳‍♂️", "🧕", "👮‍♀️", "👮‍♂️", "👷‍♀️", "👷‍♂️", "💂‍♀️", "💂‍♂️", "🕵️‍♀️", "🕵️‍♂️", "👩‍⚕️", "👨‍⚕️", "👩‍🌾", "👨‍🌾", "👩‍🍳", "👨‍🍳", "👩‍🎓", "👨‍🎓", "👩‍🎤", "👨‍🎤", "👩‍🏫", "👨‍🏫", "👩‍🏭", "👨‍🏭", "👩‍💻", "👨‍💻", "👩‍💼", "👨‍💼", "👩‍🔧", "👨‍🔧", "👩‍🔬", "👨‍🔬", "👩‍🎨", "👨‍🎨", "👩‍🚒", "👨‍🚒", "👩‍✈️", "👨‍✈️", "👩‍🚀", "👨‍🚀", "👩‍⚖️", "👨‍⚖️"),
            "Hearts" to listOf("❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "💌", "💋", "💯", "💢", "💥", "💫", "💦", "💨", "🕳️", "💬", "🗨️", "🗯️", "💭", "💤"),
            "Animals" to listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐻‍❄️", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🐤", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞", "🐜", "🪰", "🪲", "🪳", "🦟", "🦗", "🕷️", "🕸️", "🦂", "🐢", "🐍", "🦎", "🦖", "🦕", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳", "🐋", "🦈", "🦭", "🐊", "🐅", "🐆", "🦓", "🦍", "🦧", "🦣", "🐘", "🦛", "🦏", "🐪", "🐫", "🦒", "🦘", "🦬", "🐃", "🐂", "🐄", "🐎", "🐖", "🐏", "🐑", "🦙", "🐐", "🦌", "🐕", "🐩", "🦮", "🐕‍🦺", "🐈", "🐈‍⬛", "🪶", "🐓", "🦃", "🦤", "🦚", "🦜", "🦢", "🦩", "🕊️"),
            "Food" to listOf("🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦", "🥬", "🥒", "🌶️", "🫑", "🌽", "🥕", "🫒", "🧄", "🧅", "🥔", "🍠", "🥐", "🥯", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳", "🧈", "🥞", "🧇", "🥓", "🥩", "🍗", "🍖", "🦴", "🌭", "🍔", "🍟", "🍕", "🫓", "🥪", "🥙", "🧆", "🌮", "🌯", "🫔", "🥗", "🥘", "🫕", "🥫", "🍝", "🍜", "🍲", "🍛", "🍣", "🍱", "🥟", "🦪", "🍤", "🍙", "🍚", "🍘", "🍥", "🥠", "🥮", "🍢", "🍡", "🍧", "🍨", "🍦", "🥧", "🧁", "🍰", "🎂", "🍮", "🍭", "🍬", "🍫", "🍿", "🍩", "🍪", "🌰", "🥜", "🍯", "🥛", "🍼", "☕", "🫖", "🍵", "🧃", "🥤", "🧋", "🍶", "🍺", "🍻", "🥂", "🍷", "🥃", "🍸", "🍹", "🧉", "🍾", "🧊"),
            "Objects" to listOf("⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱", "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🪃", "🥅", "⛳", "🪁", "🏹", "🎣", "🤿", "🥊", "🥋", "🎽", "🛹", "🛼", "🛷", "⛸️", "🥌", "🎿", "⛷️", "🏂", "🪂", "🏋️‍♀️", "🏋️‍♂️", "🤼‍♀️", "🤼‍♂️", "🤸‍♀️", "🤸‍♂️", "⛹️‍♀️", "⛹️‍♂️", "🤺", "🤾‍♀️", "🤾‍♂️", "🏌️‍♀️", "🏌️‍♂️", "🏇", "🧘‍♀️", "🧘‍♂️", "🏄‍♀️", "🏄‍♂️", "🏊‍♀️", "🏊‍♂️", "🤽‍♀️", "🤽‍♂️", "🚣‍♀️", "🚣‍♂️", "🧗‍♀️", "🧗‍♂️", "🚵‍♀️", "🚵‍♂️", "🚴‍♀️", "🚴‍♂️", "🏆", "🥇", "🥈", "🥉", "🏅", "🎖️", "🏵️", "🎗️", "🎫", "🎟️", "🎪", "🤹", "🎭", "🩰", "🎨", "🎬", "🎤", "🎧", "🎼", "🎹", "🥁", "🪘", "🎷", "🎺", "🪗", "🎸", "🪕", "🎻", "🎲", "♟️", "🎯", "🎳", "🎮", "🎰", "🧩")
        )
    }

    val displayList = remember(selectedCategory, searchQuery, recentEmojis) {
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            allEmojis.values.flatten().distinct().filter { emoji ->
                // Basic match
                true
            }.take(40)
        } else if (selectedCategory == "Recents") {
            if (recentEmojis.isNotEmpty()) recentEmojis else listOf("😀", "❤️", "🔥", "👍", "✨", "🚀", "🎉", "🙏")
        } else {
            allEmojis[selectedCategory] ?: emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(themePalette.keyBackgroundPressed)
                    .clickable { onBackToAlpha() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = themePalette.keyText,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(themePalette.keyBackground)
                    .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Emojis",
                    tint = themePalette.accentPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (searchQuery.isEmpty()) "Search emojis..." else searchQuery,
                    style = TextStyle(
                        color = if (searchQuery.isEmpty()) themePalette.keySubtext.copy(alpha = 0.6f) else themePalette.keyText,
                        fontSize = 11.5.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = themePalette.keySubtext,
                        modifier = Modifier
                            .size(15.dp)
                            .clickable { onQueryChange("") }
                    )
                }
            }
        }

        // CATEGORY CHIPS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val categories = listOf("Recents", "Smileys", "Gestures", "Hearts", "Animals", "Food", "Objects")
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat && searchQuery.isEmpty()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) themePalette.accentPrimary else themePalette.keyBackgroundPressed)
                        .clickable {
                            selectedCategory = cat
                            if (searchQuery.isNotEmpty()) onQueryChange("")
                        }
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else themePalette.keySubtext,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        // EMOJI GRID
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth().height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(displayList) { emoji ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onSelectEmoji(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun ClipboardPanel(
    themePalette: ImeThemePalette,
    onSelectClip: (String) -> Unit,
    onBackToAlpha: () -> Unit
) {
    val context = LocalContext.current
    var clips by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipList = mutableListOf<String>()
        val primary = clipboard?.primaryClip
        if (primary != null && primary.itemCount > 0) {
            for (i in 0 until primary.itemCount) {
                val text = primary.getItemAt(i).text?.toString()
                if (!text.isNullOrBlank()) {
                    clipList.add(text)
                }
            }
        }
        if (clipList.isEmpty()) {
            clipList.add("Welcome to Aura Keyboard!")
            clipList.add("Fast, private & AI-powered typing.")
        }
        clips = clipList
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
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
                        .clickable { onBackToAlpha() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = themePalette.keyText, modifier = Modifier.size(15.dp))
                }
                Text("Clipboard History", style = MaterialTheme.typography.titleSmall.copy(color = themePalette.keyText, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(clips) { clip ->
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(themePalette.keyBackground)
                        .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { onSelectClip(clip) }
                        .padding(6.dp)
                ) {
                    Text(
                        text = clip,
                        style = TextStyle(color = themePalette.keyText, fontSize = 11.sp),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun TranslationPanel(
    themePalette: ImeThemePalette,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onInsertTranslation: (String) -> Unit,
    onBackToAlpha: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var targetLanguage by remember { mutableStateOf("Spanish") }
    var translatedOutput by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }

    fun runTranslate() {
        if (inputText.isBlank()) return
        isTranslating = true
        coroutineScope.launch {
            val res = GeminiService.processAiAction(
                actionType = AIActionType.TRANSLATE,
                input = inputText,
                tone = ToneOption.CONCISE,
                targetLanguage = targetLanguage
            )
            isTranslating = false
            res.onSuccess { translatedOutput = it.trim() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
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
                        .clickable { onBackToAlpha() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = themePalette.keyText, modifier = Modifier.size(15.dp))
                }
                Text("Live Translation", style = MaterialTheme.typography.titleSmall.copy(color = themePalette.keyText, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(themePalette.accentPrimary)
                    .clickable { runTranslate() }
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("Translate", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        // TARGET LANGUAGE SELECTOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val languages = listOf("Spanish", "French", "German", "Japanese", "Chinese", "Hindi", "Arabic", "Portuguese")
            languages.forEach { lang ->
                val isSelected = targetLanguage == lang
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) themePalette.accentPrimary else themePalette.keyBackgroundPressed)
                        .clickable { targetLanguage = lang }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(lang, color = if (isSelected) Color.White else themePalette.keySubtext, fontSize = 9.5.sp)
                }
            }
        }

        // PREVIEW / RESULT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(themePalette.keyBackground)
                .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(6.dp)
        ) {
            if (translatedOutput.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(translatedOutput, color = themePalette.keyText, fontSize = 11.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(themePalette.accentPrimary)
                            .clickable { onInsertTranslation(translatedOutput) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Insert Translation", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text(
                    text = if (inputText.isEmpty()) "Type text on keyboard to translate to $targetLanguage..." else "Input: $inputText",
                    color = if (inputText.isEmpty()) themePalette.keySubtext.copy(alpha = 0.6f) else themePalette.keyText,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun TextEditingPanel(
    themePalette: ImeThemePalette,
    onDpadMove: (Int) -> Unit,
    onSelectAll: () -> Unit,
    onCopy: () -> Unit,
    onCut: () -> Unit,
    onPaste: () -> Unit,
    onUndo: () -> Unit,
    onBackToAlpha: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
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
                        .clickable { onBackToAlpha() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = themePalette.keyText, modifier = Modifier.size(15.dp))
                }
                Text("Text Editing & Cursor", style = MaterialTheme.typography.titleSmall.copy(color = themePalette.keyText, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
        }

        // CONTROLS GRID: D-Pad & Actions
        Row(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ACTION BUTTONS (Select All, Copy, Cut, Paste, Undo)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onSelectAll() }, contentAlignment = Alignment.Center) {
                        Text("Select All", fontSize = 10.sp, color = themePalette.keyText)
                    }
                    Box(modifier = Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onCopy() }, contentAlignment = Alignment.Center) {
                        Text("Copy", fontSize = 10.sp, color = themePalette.keyText)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onCut() }, contentAlignment = Alignment.Center) {
                        Text("Cut", fontSize = 10.sp, color = themePalette.keyText)
                    }
                    Box(modifier = Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onPaste() }, contentAlignment = Alignment.Center) {
                        Text("Paste", fontSize = 10.sp, color = themePalette.keyText)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(28.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackgroundPressed).clickable { onUndo() }, contentAlignment = Alignment.Center) {
                    Text("Undo", fontSize = 10.sp, color = themePalette.keySubtext)
                }
            }

            // D-PAD
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // UP
                Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onDpadMove(KeyEvent.KEYCODE_DPAD_UP) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = themePalette.keyText, modifier = Modifier.size(18.dp))
                }
                // LEFT / RIGHT
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onDpadMove(KeyEvent.KEYCODE_DPAD_LEFT) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = themePalette.keyText, modifier = Modifier.size(18.dp))
                    }
                    Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onDpadMove(KeyEvent.KEYCODE_DPAD_RIGHT) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = themePalette.keyText, modifier = Modifier.size(18.dp))
                    }
                }
                // DOWN
                Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onDpadMove(KeyEvent.KEYCODE_DPAD_DOWN) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = themePalette.keyText, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun ThemesPanel(
    themePalette: ImeThemePalette,
    selectedThemeId: String,
    onSelectTheme: (String) -> Unit,
    onBackToAlpha: () -> Unit
) {
    val themes = listOf(
        Pair("dynamic", "Dynamic Color"),
        Pair("midnight_bloom", "Midnight Amethyst"),
        Pair("cyber_teal", "Emerald Slate"),
        Pair("titanium_dark", "Titanium OLED"),
        Pair("minimalist", "Pristine Light"),
        Pair("warm_sand", "Warm Sand")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
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
                        .clickable { onBackToAlpha() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = themePalette.keyText, modifier = Modifier.size(15.dp))
                }
                Text("Keyboard Themes", style = MaterialTheme.typography.titleSmall.copy(color = themePalette.keyText, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(themes) { (id, name) ->
                val isSelected = selectedThemeId == id
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) themePalette.accentPrimary else themePalette.keyBackground)
                        .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { onSelectTheme(id) }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        color = if (isSelected) Color.White else themePalette.keyText,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun WritingAssistantPanel(
    themePalette: ImeThemePalette,
    getCurrentInputText: () -> String,
    onReplaceText: (String, Int) -> Unit,
    onBackToAlpha: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var activeOutput by remember { mutableStateOf<String?>(null) }
    var fetchedLength by remember { mutableIntStateOf(0) }

    fun runTone(tone: ToneOption) {
        val appText = getCurrentInputText().trim()
        if (appText.isEmpty()) return
        fetchedLength = appText.length
        isProcessing = true
        activeOutput = null
        coroutineScope.launch {
            val res = GeminiService.processAiAction(
                actionType = AIActionType.REWRITE,
                input = appText,
                tone = tone
            )
            isProcessing = false
            res.onSuccess { activeOutput = it.trim() }
        }
    }

    fun runFixGrammar() {
        val appText = getCurrentInputText().trim()
        if (appText.isEmpty()) return
        fetchedLength = appText.length
        isProcessing = true
        activeOutput = null
        coroutineScope.launch {
            val res = GeminiService.processAiAction(
                actionType = AIActionType.FIX,
                input = appText,
                tone = ToneOption.CONCISE
            )
            isProcessing = false
            res.onSuccess { activeOutput = it.trim() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
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
                        .clickable { onBackToAlpha() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = themePalette.keyText, modifier = Modifier.size(15.dp))
                }
                Text("Writing Assistant", style = MaterialTheme.typography.titleSmall.copy(color = themePalette.keyText, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
        }

        // QUICK TONE REWRITE PILLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(themePalette.accentPrimary)
                    .clickable { runFixGrammar() }
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text("Fix Grammar", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            ToneOption.entries.forEach { tone ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(themePalette.keyBackgroundPressed)
                        .clickable { runTone(tone) }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(tone.label, color = themePalette.keyText, fontSize = 10.sp)
                }
            }
        }

        // PREVIEW / RESULT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(themePalette.keyBackground)
                .border(0.5.dp, themePalette.border.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(6.dp)
        ) {
            if (activeOutput != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(activeOutput ?: "", color = themePalette.keyText, fontSize = 11.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(themePalette.accentPrimary)
                            .clickable {
                                activeOutput?.let { onReplaceText(it, fetchedLength) }
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Replace App Text", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text(
                    text = if (isProcessing) "Refining text with Gemini AI..." else "Select an action above to enhance the active text in your app.",
                    color = themePalette.keySubtext,
                    fontSize = 11.sp
                )
            }
        }
    }
}
