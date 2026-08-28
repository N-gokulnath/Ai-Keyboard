package com.example.ime.ui

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.view.KeyEvent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.gemini.GeminiService
import com.example.ime.engine.KeyAction
import com.example.ime.engine.KeyboardMode
import com.example.ime.engine.ShiftState
import com.example.model.AIActionType
import com.example.model.KeyboardSettings
import com.example.model.ToneOption
import com.example.model.WritingProfile
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Production-Grade Aura Keyboard Main View.
 * 
 * Features:
 * - Liquid UI Glass aesthetic with zero blue background
 * - Top menu bar with exactly 4 centered primary quick icons
 * - In-place replacement for Tools/Menus (matching keyboard height, no stacking)
 * - Full-height expanded canvas for Media (GIFs, Stickers, Emojis) and AI Studio
 * - Support for Apple Cupertino, Google Material You, and Liquid themes
 */
@Composable
fun AuraKeyboardView(
    themePalette: ImeThemePalette,
    shiftState: ShiftState,
    keyboardMode: KeyboardMode,
    suggestions: List<String>,
    actionInfo: KeyAction,
    isPrivateMode: Boolean,
    settings: KeyboardSettings,
    profile: WritingProfile = WritingProfile(),
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
    onAiAction: (AIActionType) -> Unit = {},
    onCommitAiText: (String) -> Unit = {},
    onReplaceAiText: (String, Int) -> Unit = { _, _ -> },
    onOpenSettings: () -> Unit = {},
    onThemeChange: (String) -> Unit = {},
    onToggleFloatingMode: () -> Unit = {},
    onFloatingScaleChange: (Float) -> Unit = {},
    onFloatingHeightScaleChange: (Float) -> Unit = {},
    onAddRecentEmoji: (String) -> Unit = {},
    onUpdatePinnedTools: (List<String>) -> Unit = {},
    onCommitRichContent: ((Uri, String, String, String?) -> Boolean)? = null,
    onCommitKlipyMedia: ((com.example.data.klipy.KlipyMediaItem) -> Unit)? = null,
    onDpadMove: (Int) -> Unit = {},
    onSelectAll: () -> Unit = {},
    onCopy: () -> Unit = {},
    onCut: () -> Unit = {},
    onPaste: () -> Unit = {},
    onUndo: () -> Unit = {},
    onHeightScaleChange: (Float) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    // State for search queries
    var gifSearchQuery by remember { mutableStateOf("") }
    var stickerSearchQuery by remember { mutableStateOf("") }
    var emojiSearchQuery by remember { mutableStateOf("") }
    var aiPromptText by remember { mutableStateOf("") }
    var translationInputText by remember { mutableStateOf("") }

    // Internal In-Place Tools Menu state
    var showToolsMenu by remember { mutableStateOf(false) }

    // Floating drag offset
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val effectiveBottomInset = if (settings.isFloatingMode) 0.dp else {
        if (settings.bottomInsetPaddingDp > 0) settings.bottomInsetPaddingDp.dp else maxOf(systemNavInsetDp, 16.dp)
    }

    // Key height scaling
    val keyHeight = (42.dp * settings.keyboardHeightScale).coerceIn(34.dp, 56.dp)

    // Check if current mode is an EXPANDED full-height media/AI mode
    val isExpandedMediaOrAiMode = when (keyboardMode) {
        KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE,
        KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE, KeyboardMode.STICKER_SEARCH_MODE,
        KeyboardMode.EMOJI, KeyboardMode.EMOJI_SEARCH_MODE,
        KeyboardMode.AI_STUDIO, KeyboardMode.AI_CHAT_MODE -> true
        else -> false
    }

    // Check if current mode is an IN-PLACE tool (replaces the key rows)
    val isInPlaceToolMode = when (keyboardMode) {
        KeyboardMode.TOOLBAR_CUSTOMIZATION_MODE,
        KeyboardMode.CLIPBOARD,
        KeyboardMode.TRANSLATION,
        KeyboardMode.WRITING_ASSISTANT,
        KeyboardMode.TEXT_EDITING,
        KeyboardMode.THEMES -> true
        else -> showToolsMenu
    }

    // Key typing interceptor for expanded search / AI input
    val handleActiveKeyChar: (String) -> Unit = { char ->
        when (keyboardMode) {
            KeyboardMode.AI_STUDIO, KeyboardMode.AI_CHAT_MODE -> {
                aiPromptText += char
            }
            KeyboardMode.EMOJI, KeyboardMode.EMOJI_SEARCH_MODE -> {
                emojiSearchQuery += char
            }
            KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE -> {
                gifSearchQuery += char
            }
            KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE, KeyboardMode.STICKER_SEARCH_MODE -> {
                stickerSearchQuery += char
            }
            else -> onKeyChar(char)
        }
    }

    val handleActiveBackspace: () -> Unit = {
        when (keyboardMode) {
            KeyboardMode.AI_STUDIO, KeyboardMode.AI_CHAT_MODE -> {
                if (aiPromptText.isNotEmpty()) aiPromptText = aiPromptText.dropLast(1)
            }
            KeyboardMode.EMOJI, KeyboardMode.EMOJI_SEARCH_MODE -> {
                if (emojiSearchQuery.isNotEmpty()) emojiSearchQuery = emojiSearchQuery.dropLast(1)
            }
            KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE -> {
                if (gifSearchQuery.isNotEmpty()) gifSearchQuery = gifSearchQuery.dropLast(1)
            }
            KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE, KeyboardMode.STICKER_SEARCH_MODE -> {
                if (stickerSearchQuery.isNotEmpty()) stickerSearchQuery = stickerSearchQuery.dropLast(1)
            }
            else -> onBackspace()
        }
    }

    val handleActiveSpace: () -> Unit = {
        when (keyboardMode) {
            KeyboardMode.AI_STUDIO, KeyboardMode.AI_CHAT_MODE -> {
                aiPromptText += " "
            }
            KeyboardMode.EMOJI, KeyboardMode.EMOJI_SEARCH_MODE -> {
                emojiSearchQuery += " "
            }
            KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE -> {
                gifSearchQuery += " "
            }
            KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE, KeyboardMode.STICKER_SEARCH_MODE -> {
                stickerSearchQuery += " "
            }
            else -> onSpace()
        }
    }

    // Outer Container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (settings.isFloatingMode) {
                    Modifier
                        .offset { IntOffset(dragOffsetX.roundToInt(), dragOffsetY.roundToInt()) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                } else {
                    Modifier
                }
            )
            .testTag("aura_keyboard_view")
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(if (settings.isFloatingMode) RoundedCornerShape(16.dp) else RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
            color = themePalette.background,
            tonalElevation = if (settings.isFloatingMode) 8.dp else 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Subtle liquid glass perimeter luminescence
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    themePalette.border.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            size = size.copy(height = 3.dp.toPx())
                        )
                    }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // -------------------------------------------------------------
                // 1. FLOATING CONTROL BAR (Only visible in Floating Mode)
                // -------------------------------------------------------------
                if (settings.isFloatingMode) {
                    FloatingTopControlBar(
                        themePalette = themePalette,
                        onResetPosition = {
                            dragOffsetX = 0f
                            dragOffsetY = 0f
                        },
                        onDock = onToggleFloatingMode
                    )
                }

                // -------------------------------------------------------------
                // 2. FULL-HEIGHT EXPANDED MEDIA & AI PANELS (Top Pane + Lower Keyboard)
                // -------------------------------------------------------------
                if (isExpandedMediaOrAiMode) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Upper Interactive Pane
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(245.dp)
                        ) {
                            when (keyboardMode) {
                                KeyboardMode.GIF, KeyboardMode.GIF_SEARCH_MODE -> {
                                    GifKeyboardLayout(
                                        themePalette = themePalette,
                                        searchQuery = gifSearchQuery,
                                        onQueryChange = { gifSearchQuery = it },
                                        onSelectGif = { item ->
                                            if (onCommitKlipyMedia != null) {
                                                onCommitKlipyMedia(item)
                                            } else {
                                                val mediaUrl = item.resolveDirectMediaUrl(preferSmall = false)
                                                val title = item.title ?: "GIF"
                                                val committed = onCommitRichContent?.invoke(Uri.parse(mediaUrl), "image/gif", title, mediaUrl) ?: false
                                                if (!committed && mediaUrl.isNotBlank()) {
                                                    onKeyChar(mediaUrl)
                                                }
                                            }
                                            onModeChange(KeyboardMode.NORMAL_MODE)
                                        },
                                        onBackToAlpha = { onModeChange(KeyboardMode.NORMAL_MODE) }
                                    )
                                }

                                KeyboardMode.STICKERS, KeyboardMode.STICKER_MODE, KeyboardMode.STICKER_SEARCH_MODE -> {
                                    StickerKeyboardLayout(
                                        themePalette = themePalette,
                                        searchQuery = stickerSearchQuery,
                                        onQueryChange = { stickerSearchQuery = it },
                                        onSelectSticker = { item ->
                                            if (onCommitKlipyMedia != null) {
                                                onCommitKlipyMedia(item)
                                            } else {
                                                val mediaUrl = item.resolveDirectMediaUrl(preferSmall = false)
                                                val title = item.title ?: "Sticker"
                                                val committed = onCommitRichContent?.invoke(Uri.parse(mediaUrl), "image/webp", title, mediaUrl) ?: false
                                                if (!committed && mediaUrl.isNotBlank()) {
                                                    onKeyChar(mediaUrl)
                                                }
                                            }
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
                                else -> {}
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Lower Full Keyboard for direct instruction typing
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            AlphabetLayout(
                                themePalette = themePalette,
                                shiftState = shiftState,
                                keyHeight = keyHeight * 0.95f,
                                onKeyChar = handleActiveKeyChar,
                                onBackspace = handleActiveBackspace,
                                onShiftToggle = onShiftToggle,
                                onModeChange = onModeChange,
                                actionInfo = actionInfo,
                                onActionClick = onActionClick,
                                onSpace = handleActiveSpace,
                                onSwitchIme = onSwitchIme,
                                settings = settings
                            )
                        }
                    }
                } else if (isInPlaceToolMode) {
                    // ---------------------------------------------------------
                    // 3. IN-PLACE TOOLS & MENUS (Replaces keyboard keys in-place)
                    // ---------------------------------------------------------
                    if (showToolsMenu) {
                        InPlaceToolsMenuGrid(
                            themePalette = themePalette,
                            onSelectTool = { tool ->
                                showToolsMenu = false
                                when (tool.id) {
                                    "floating" -> onToggleFloatingMode()
                                    "settings" -> onOpenSettings()
                                    else -> onModeChange(tool.targetMode)
                                }
                            },
                            onClose = { showToolsMenu = false }
                        )
                    } else {
                        when (keyboardMode) {
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

                            KeyboardMode.TOOLBAR_CUSTOMIZATION_MODE -> {
                                ToolbarCustomizerLayout(
                                    themePalette = themePalette,
                                    pinnedToolIds = if (settings.pinnedToolIds.isEmpty()) KeyboardTools.DEFAULT_PINNED_IDS else settings.pinnedToolIds,
                                    onUpdatePinnedTools = onUpdatePinnedTools,
                                    onDone = { onModeChange(KeyboardMode.NORMAL_MODE) }
                                )
                            }

                            else -> {}
                        }
                    }
                } else {
                    // ---------------------------------------------------------
                    // 4. STANDARD KEYBOARD INTERFACE: TOP MENU BAR + KEY ROWS
                    // ---------------------------------------------------------

                    // TOP MENU BAR (Clean, compact: Radiant AI, Grid Menu, Pinned Tools, Settings)
                    TopMenuBar(
                        themePalette = themePalette,
                        suggestions = suggestions,
                        showSuggestions = settings.showSuggestions,
                        pinnedToolIds = if (settings.pinnedToolIds.isEmpty()) KeyboardTools.DEFAULT_PINNED_IDS else settings.pinnedToolIds,
                        isPrivateMode = isPrivateMode,
                        onOpenToolsMenu = { showToolsMenu = true },
                        onModeChange = onModeChange,
                        onSuggestionClick = onSuggestionClick,
                        onOpenSettings = onOpenSettings
                    )

                    // KEYBOARD KEYS AREA (Alphabet or Symbols)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        verticalArrangement = Arrangement.spacedBy(3.5.dp)
                    ) {
                        // Optional Number Row
                        if (settings.showNumberRow && keyboardMode != KeyboardMode.SYMBOLS && keyboardMode != KeyboardMode.MORE_SYMBOLS) {
                            NumberRow(
                                themePalette = themePalette,
                                height = (keyHeight * 0.85f),
                                onKeyChar = onKeyChar
                            )
                        }

                        // Standard 4 Rows of Keys
                        when (keyboardMode) {
                            KeyboardMode.SYMBOLS -> {
                                SymbolsLayout(
                                    themePalette = themePalette,
                                    keyHeight = keyHeight,
                                    onKeyChar = onKeyChar,
                                    onBackspace = onBackspace,
                                    onModeChange = onModeChange,
                                    actionInfo = actionInfo,
                                    onActionClick = onActionClick,
                                    onSpace = onSpace,
                                    settings = settings
                                )
                            }
                            KeyboardMode.MORE_SYMBOLS -> {
                                MoreSymbolsLayout(
                                    themePalette = themePalette,
                                    keyHeight = keyHeight,
                                    onKeyChar = onKeyChar,
                                    onBackspace = onBackspace,
                                    onModeChange = onModeChange,
                                    actionInfo = actionInfo,
                                    onActionClick = onActionClick,
                                    onSpace = onSpace,
                                    settings = settings
                                )
                            }
                            else -> {
                                AlphabetLayout(
                                    themePalette = themePalette,
                                    shiftState = shiftState,
                                    keyHeight = keyHeight,
                                    onKeyChar = onKeyChar,
                                    onBackspace = onBackspace,
                                    onShiftToggle = onShiftToggle,
                                    onModeChange = onModeChange,
                                    actionInfo = actionInfo,
                                    onActionClick = onActionClick,
                                    onSpace = onSpace,
                                    onSwitchIme = onSwitchIme,
                                    settings = settings
                                )
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // 5. BOTTOM DRAG HANDLE (Floating Mode) OR INSET SPACER (Docked)
                // -------------------------------------------------------------
                if (settings.isFloatingMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .padding(top = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(themePalette.keySubtext.copy(alpha = 0.45f))
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        dragOffsetX = (dragOffsetX + dragAmount.x).coerceIn(-400f, 400f)
                                        dragOffsetY = (dragOffsetY + dragAmount.y).coerceIn(-600f, 150f)
                                    }
                                }
                        )
                    }
                } else if (effectiveBottomInset > 0.dp) {
                    Spacer(modifier = Modifier.height(effectiveBottomInset))
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TOP MENU BAR (RADIANT AI, GRID MENU, PINNED TOOLS, SETTINGS)
// -----------------------------------------------------------------------------

@Composable
private fun TopMenuBar(
    themePalette: ImeThemePalette,
    suggestions: List<String>,
    showSuggestions: Boolean,
    pinnedToolIds: List<String>,
    isPrivateMode: Boolean,
    onOpenToolsMenu: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val hasSuggestions = suggestions.isNotEmpty() && showSuggestions
    val pinnedTools = remember(pinnedToolIds) {
        val list = pinnedToolIds.mapNotNull { KeyboardTools.getToolById(it) }
        if (list.isNotEmpty()) list.take(5) else listOf(
            KeyboardTools.TOOL_CLIPBOARD,
            KeyboardTools.TOOL_WRITING_ASSISTANT,
            KeyboardTools.TOOL_THEMES,
            KeyboardTools.TOOL_TRANSLATE,
            KeyboardTools.TOOL_EMOJI
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(themePalette.candidateBarBg)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. LEFT: Radiant AI Button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFFFF7A00), Color(0xFFFF007A), Color(0xFF7000FF))))
                .clickable { onModeChange(KeyboardMode.AI_STUDIO) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Assistant",
                tint = Color.White,
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(modifier = Modifier.width(3.dp))

        // 2. Grid Menu Button
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(themePalette.keyBackground)
                .clickable { onOpenToolsMenu() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.GridView,
                contentDescription = "Tools Menu",
                tint = themePalette.accentPrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        // 3. CENTER: PINNED TOOLS OR WORD SUGGESTIONS
        if (hasSuggestions) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                suggestions.forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(themePalette.keyBackground)
                            .clickable { onSuggestionClick(suggestion) }
                            .padding(horizontal = 9.dp, vertical = 4.5.dp)
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
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                pinnedTools.forEach { tool ->
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(42.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(themePalette.keyBackground)
                            .border(0.5.dp, themePalette.border.copy(alpha = 0.25f), RoundedCornerShape(7.dp))
                            .clickable { onModeChange(tool.targetMode) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.title,
                            tint = themePalette.keyText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(3.dp))

        // 4. RIGHT: Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            if (isPrivateMode) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x3310B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Private Mode",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(themePalette.keyBackground)
                    .clickable { onOpenSettings() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = themePalette.keySubtext,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
private fun MenuShortcutIcon(
    icon: ImageVector,
    label: String,
    isGradient: Boolean,
    themePalette: ImeThemePalette,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(30.dp)
            .width(52.dp)
            .clip(RoundedCornerShape(7.dp))
            .then(
                if (isGradient) {
                    Modifier.background(Brush.horizontalGradient(listOf(themePalette.accentPrimary, themePalette.accentSecondary)))
                } else {
                    Modifier
                        .background(themePalette.keyBackground)
                        .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(7.dp))
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isGradient) Color.White else themePalette.accentPrimary,
            modifier = Modifier.size(17.dp)
        )
    }
}

// -----------------------------------------------------------------------------
// IN-PLACE TOOLS MENU GRID (REPLACES KEYBOARD IN-PLACE)
// -----------------------------------------------------------------------------

@Composable
private fun InPlaceToolsMenuGrid(
    themePalette: ImeThemePalette,
    onSelectTool: (KeyboardToolItem) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
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
                Text(
                    text = "Keyboard Tools & Options",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = themePalette.keyText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(themePalette.keyBackground)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Menu",
                    tint = themePalette.keySubtext,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Tools Grid (2 rows x 4 columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(KeyboardTools.ALL_TOOLS, key = { it.id }) { tool ->
                Box(
                    modifier = Modifier
                        .height(84.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(themePalette.keyBackground)
                        .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable { onSelectTool(tool) }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(themePalette.keyBackgroundPressed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = tool.title,
                                tint = themePalette.accentPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tool.shortName,
                            style = TextStyle(
                                color = themePalette.keyText,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// FLOATING TOP CONTROL BAR
// -----------------------------------------------------------------------------

@Composable
private fun FloatingTopControlBar(
    themePalette: ImeThemePalette,
    onResetPosition: () -> Unit,
    onDock: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PictureInPicture,
                contentDescription = null,
                tint = themePalette.accentPrimary,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "Floating Keyboard",
                color = themePalette.keySubtext,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(themePalette.keyBackgroundPressed)
                    .clickable { onResetPosition() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Position",
                    tint = themePalette.keyText,
                    modifier = Modifier.size(12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(themePalette.accentPrimary)
                    .clickable { onDock() }
                    .padding(horizontal = 6.dp, vertical = 2.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerticalAlignBottom,
                        contentDescription = "Dock",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                    Text("Dock", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// NUMBER ROW
// -----------------------------------------------------------------------------

@Composable
private fun NumberRow(
    themePalette: ImeThemePalette,
    height: Dp,
    onKeyChar: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        numbers.forEach { num ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(height)
                    .clip(RoundedCornerShape(6.dp))
                    .background(themePalette.specialKeyBackground)
                    .border(0.5.dp, themePalette.border.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .clickable { onKeyChar(num) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = num,
                    style = TextStyle(
                        color = themePalette.keyText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// ALPHABET KEYBOARD LAYOUT (QWERTY)
// -----------------------------------------------------------------------------

@Composable
private fun AlphabetLayout(
    themePalette: ImeThemePalette,
    shiftState: ShiftState,
    keyHeight: Dp,
    onKeyChar: (String) -> Unit,
    onBackspace: () -> Unit,
    onShiftToggle: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    actionInfo: KeyAction,
    onActionClick: () -> Unit,
    onSpace: () -> Unit,
    onSwitchIme: () -> Unit,
    settings: KeyboardSettings
) {
    val isUpper = shiftState == ShiftState.SHIFT || shiftState == ShiftState.CAPS_LOCK

    // Row 1: Q W E R T Y U I O P
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        row1.forEach { char ->
            val displayChar = if (isUpper) char.uppercase() else char
            ImeKey(
                label = displayChar,
                themePalette = themePalette,
                height = keyHeight,
                modifier = Modifier.weight(1f),
                onClick = { onKeyChar(displayChar) }
            )
        }
    }

    // Row 2: A S D F G H J K L
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        row2.forEach { char ->
            val displayChar = if (isUpper) char.uppercase() else char
            ImeKey(
                label = displayChar,
                themePalette = themePalette,
                height = keyHeight,
                modifier = Modifier.weight(1f),
                onClick = { onKeyChar(displayChar) }
            )
        }
    }

    // Row 3: Shift, Z X C V B N M, Backspace
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // SHIFT KEY
        ImeSpecialKey(
            icon = Icons.Default.KeyboardCapslock,
            label = "Shift",
            themePalette = themePalette,
            height = keyHeight,
            isActive = shiftState != ShiftState.OFF,
            modifier = Modifier.weight(1.35f),
            onClick = onShiftToggle
        )

        row3.forEach { char ->
            val displayChar = if (isUpper) char.uppercase() else char
            ImeKey(
                label = displayChar,
                themePalette = themePalette,
                height = keyHeight,
                modifier = Modifier.weight(1f),
                onClick = { onKeyChar(displayChar) }
            )
        }

        // BACKSPACE KEY
        ImeSpecialKey(
            icon = Icons.AutoMirrored.Filled.Backspace,
            label = "Delete",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(1.35f),
            onClick = onBackspace
        )
    }

    // Row 4: ?123, Comma, Spacebar, Period, Enter/Action
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode switch to ?123
        ImeSpecialKey(
            label = "?123",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(1.3f),
            onClick = { onModeChange(KeyboardMode.SYMBOLS) }
        )

        // Switch IME / Language Globe (if enabled in settings)
        if (settings.showLanguageSwitchKey) {
            ImeSpecialKey(
                icon = Icons.Default.Language,
                themePalette = themePalette,
                height = keyHeight,
                modifier = Modifier.weight(0.85f),
                onClick = onSwitchIme
            )
        }

        // Comma
        ImeKey(
            label = ",",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(0.9f),
            onClick = { onKeyChar(",") }
        )

        // Spacebar
        Box(
            modifier = Modifier
                .weight(if (settings.showLanguageSwitchKey) 3.6f else 4.4f)
                .height(keyHeight)
                .clip(RoundedCornerShape(6.dp))
                .background(themePalette.keyBackground)
                .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .clickable { onSpace() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "English (US)",
                style = TextStyle(
                    color = themePalette.keySubtext.copy(alpha = 0.55f),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // Period
        ImeKey(
            label = ".",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(0.9f),
            onClick = { onKeyChar(".") }
        )

        // Action / Enter
        val actionIcon: ImageVector = when (actionInfo.actionId) {
            android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH -> Icons.Default.Search
            android.view.inputmethod.EditorInfo.IME_ACTION_SEND -> Icons.Default.Done
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
            Icon(
                imageVector = actionIcon,
                contentDescription = "Action",
                tint = Color.White,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// SYMBOLS LAYOUT
// -----------------------------------------------------------------------------

@Composable
private fun SymbolsLayout(
    themePalette: ImeThemePalette,
    keyHeight: Dp,
    onKeyChar: (String) -> Unit,
    onBackspace: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    actionInfo: KeyAction,
    onActionClick: () -> Unit,
    onSpace: () -> Unit,
    settings: KeyboardSettings
) {
    val row1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val row2 = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/")
    val row3 = listOf("=", "*", "\"", "'", ":", ";", "!", "?")

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        row1.forEach { sym ->
            ImeKey(label = sym, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { onKeyChar(sym) })
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        row2.forEach { sym ->
            ImeKey(label = sym, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { onKeyChar(sym) })
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        ImeSpecialKey(
            label = "=\\<",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(1.35f),
            onClick = { onModeChange(KeyboardMode.MORE_SYMBOLS) }
        )

        row3.forEach { sym ->
            ImeKey(label = sym, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { onKeyChar(sym) })
        }

        ImeSpecialKey(
            icon = Icons.AutoMirrored.Filled.Backspace,
            label = "Delete",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(1.35f),
            onClick = onBackspace
        )
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        ImeSpecialKey(
            label = "ABC",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(1.3f),
            onClick = { onModeChange(KeyboardMode.NORMAL_MODE) }
        )

        ImeKey(label = ",", themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(0.9f), onClick = { onKeyChar(",") })

        Box(
            modifier = Modifier
                .weight(3.8f)
                .height(keyHeight)
                .clip(RoundedCornerShape(6.dp))
                .background(themePalette.keyBackground)
                .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .clickable { onSpace() },
            contentAlignment = Alignment.Center
        ) {
            Text("Space", style = TextStyle(color = themePalette.keySubtext.copy(alpha = 0.5f), fontSize = 11.sp))
        }

        ImeKey(label = ".", themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(0.9f), onClick = { onKeyChar(".") })

        Box(
            modifier = Modifier
                .weight(1.4f)
                .height(keyHeight)
                .clip(RoundedCornerShape(6.dp))
                .background(themePalette.accentPrimary)
                .clickable { onActionClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.KeyboardReturn, contentDescription = "Return", tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// MORE SYMBOLS LAYOUT
// -----------------------------------------------------------------------------

@Composable
private fun MoreSymbolsLayout(
    themePalette: ImeThemePalette,
    keyHeight: Dp,
    onKeyChar: (String) -> Unit,
    onBackspace: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    actionInfo: KeyAction,
    onActionClick: () -> Unit,
    onSpace: () -> Unit,
    settings: KeyboardSettings
) {
    val row1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    val row2 = listOf("£", "¥", "€", "¢", "^", "°", "{", "}", "\\", "_")
    val row3 = listOf("[", "]", "<", ">", "«", "»", "©", "®")

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        row1.forEach { sym ->
            ImeKey(label = sym, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { onKeyChar(sym) })
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        row2.forEach { sym ->
            ImeKey(label = sym, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { onKeyChar(sym) })
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        ImeSpecialKey(
            label = "?123",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(1.35f),
            onClick = { onModeChange(KeyboardMode.SYMBOLS) }
        )

        row3.forEach { sym ->
            ImeKey(label = sym, themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(1f), onClick = { onKeyChar(sym) })
        }

        ImeSpecialKey(
            icon = Icons.AutoMirrored.Filled.Backspace,
            label = "Delete",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(1.35f),
            onClick = onBackspace
        )
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        ImeSpecialKey(
            label = "ABC",
            themePalette = themePalette,
            height = keyHeight,
            modifier = Modifier.weight(1.3f),
            onClick = { onModeChange(KeyboardMode.NORMAL_MODE) }
        )

        ImeKey(label = "<", themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(0.9f), onClick = { onKeyChar("<") })

        Box(
            modifier = Modifier
                .weight(3.8f)
                .height(keyHeight)
                .clip(RoundedCornerShape(6.dp))
                .background(themePalette.keyBackground)
                .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .clickable { onSpace() },
            contentAlignment = Alignment.Center
        ) {
            Text("Space", style = TextStyle(color = themePalette.keySubtext.copy(alpha = 0.5f), fontSize = 11.sp))
        }

        ImeKey(label = ">", themePalette = themePalette, height = keyHeight, modifier = Modifier.weight(0.9f), onClick = { onKeyChar(">") })

        Box(
            modifier = Modifier
                .weight(1.4f)
                .height(keyHeight)
                .clip(RoundedCornerShape(6.dp))
                .background(themePalette.accentPrimary)
                .clickable { onActionClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.KeyboardReturn, contentDescription = "Return", tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// CORE REUSABLE KEY COMPOSABLES
// -----------------------------------------------------------------------------

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
            .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
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
            .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
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

// -----------------------------------------------------------------------------
// EXPANDED EMOJI PANEL
// -----------------------------------------------------------------------------

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
            "Objects" to listOf("⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏐", "🥏", "🎱", "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🪃", "🥅", "⛳", "🪁", "🏹", "🎣", "🤿", "🥊", "🥋", "🎽", "🛹", "🛼", "🛷", "⛸️", "🥌", "🎿", "⛷️", "🏂", "🪂", "🏋️‍♀️", "🏋️‍♂️", "🤼‍♀️", "🤼‍♂️", "🤸‍♀️", "🤸‍♂️", "⛹️‍♀️", "⛹️‍♂️", "🤺", "🤾‍♀️", "🤾‍♂️", "🏌️‍♀️", "🏌️‍♂️", "🏇", "🧘‍♀️", "🧘‍♂️", "🏄‍♀️", "🏄‍♂️", "🏊‍♀️", "🏊‍♂️", "🤽‍♀️", "🤽‍♂️", "🚣‍♀️", "🚣‍♂️", "🧗‍♀️", "🧗‍♂️", "🚵‍♀️", "🚵‍♂️", "🚴‍♀️", "🚴‍♂️", "🏆", "🥇", "🥈", "🥉", "🏅", "🎖️", "🏵️", "🎗️", "🎫", "🎟️", "🎪", "🤹", "🎭", "🩰", "🎨", "🎬", "🎤", "🎧", "🎼", "🎹", "🥁", "🪘", "🎷", "🎺", "🪗", "🎸", "🪕", "🎻", "🎲", "♟️", "🎯", "🎳", "🎮", "🎰", "🧩")
        )
    }

    val displayList = remember(selectedCategory, searchQuery, recentEmojis) {
        if (searchQuery.isNotBlank()) {
            allEmojis.values.flatten().distinct().take(48)
        } else if (selectedCategory == "Recents") {
            if (recentEmojis.isNotEmpty()) recentEmojis else listOf("😀", "❤️", "🔥", "👍", "✨", "🚀", "🎉", "🙏")
        } else {
            allEmojis[selectedCategory] ?: emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
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
                    .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
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
                    text = if (searchQuery.isEmpty()) "Search expressive emojis..." else searchQuery,
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

        // Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
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
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else themePalette.keySubtext,
                        fontSize = 10.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Emoji Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(displayList) { emoji ->
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectEmoji(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 22.sp)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// IN-PLACE PANELS: CLIPBOARD, TRANSLATION, TEXT EDITING, THEMES, WRITING ASSISTANT
// -----------------------------------------------------------------------------

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
            .height(230.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = themePalette.keyText,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Text("Clipboard History", style = MaterialTheme.typography.titleSmall.copy(color = themePalette.keyText, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(clips) { clip ->
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(themePalette.keyBackground)
                        .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .clickable { onSelectClip(clip) }
                        .padding(8.dp)
                ) {
                    Text(
                        text = clip,
                        style = TextStyle(color = themePalette.keyText, fontSize = 11.5.sp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
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
            if (res.isSuccess) {
                translatedOutput = res.getOrNull()?.trim().orEmpty()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
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
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Translate", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Target language chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val languages = listOf("Spanish", "French", "German", "Japanese", "Chinese", "Hindi", "Arabic", "Portuguese", "Italian", "Korean")
            languages.forEach { lang ->
                val isSelected = targetLanguage == lang
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) themePalette.accentPrimary else themePalette.keyBackgroundPressed)
                        .clickable { targetLanguage = lang }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(lang, color = if (isSelected) Color.White else themePalette.keySubtext, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(themePalette.keyBackground)
                .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            if (translatedOutput.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(translatedOutput, color = themePalette.keyText, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(themePalette.accentPrimary)
                            .clickable { onInsertTranslation(translatedOutput) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Insert Translation", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text(
                    text = if (inputText.isEmpty()) "Type text or input sentence to translate into $targetLanguage..." else "Input: $inputText",
                    color = if (inputText.isEmpty()) themePalette.keySubtext.copy(alpha = 0.6f) else themePalette.keyText,
                    fontSize = 11.5.sp
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
            .height(230.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
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
                Text("Text Editing & Cursor Navigation", style = MaterialTheme.typography.titleSmall.copy(color = themePalette.keyText, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action Buttons
            Column(
                modifier = Modifier.weight(1.1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onSelectAll() }, contentAlignment = Alignment.Center) {
                        Text("Select All", fontSize = 11.sp, color = themePalette.keyText, fontWeight = FontWeight.Medium)
                    }
                    Box(modifier = Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onCopy() }, contentAlignment = Alignment.Center) {
                        Text("Copy", fontSize = 11.sp, color = themePalette.keyText, fontWeight = FontWeight.Medium)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onCut() }, contentAlignment = Alignment.Center) {
                        Text("Cut", fontSize = 11.sp, color = themePalette.keyText, fontWeight = FontWeight.Medium)
                    }
                    Box(modifier = Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackground).clickable { onPaste() }, contentAlignment = Alignment.Center) {
                        Text("Paste", fontSize = 11.sp, color = themePalette.keyText, fontWeight = FontWeight.Medium)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(34.dp).clip(RoundedCornerShape(6.dp)).background(themePalette.keyBackgroundPressed).clickable { onUndo() }, contentAlignment = Alignment.Center) {
                    Text("Undo", fontSize = 11.sp, color = themePalette.keySubtext)
                }
            }

            // D-Pad
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(themePalette.keyBackground).clickable { onDpadMove(KeyEvent.KEYCODE_DPAD_UP) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = themePalette.keyText, modifier = Modifier.size(20.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(themePalette.keyBackground).clickable { onDpadMove(KeyEvent.KEYCODE_DPAD_LEFT) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = themePalette.keyText, modifier = Modifier.size(20.dp))
                    }
                    Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(themePalette.keyBackground).clickable { onDpadMove(KeyEvent.KEYCODE_DPAD_RIGHT) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = themePalette.keyText, modifier = Modifier.size(20.dp))
                    }
                }
                Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(themePalette.keyBackground).clickable { onDpadMove(KeyEvent.KEYCODE_DPAD_DOWN) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = themePalette.keyText, modifier = Modifier.size(20.dp))
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
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
                Text("Keyboard Themes & Styles", style = MaterialTheme.typography.titleSmall.copy(color = themePalette.keyText, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(KeyboardThemePalette.ALL_THEMES) { themeItem ->
                val isSelected = selectedThemeId == themeItem.id
                Box(
                    modifier = Modifier
                        .height(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) themePalette.accentPrimary else themePalette.keyBackground)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.5.dp,
                            color = if (isSelected) Color.White else themePalette.border.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectTheme(themeItem.id) }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = themeItem.name,
                            color = if (isSelected) Color.White else themePalette.keyText,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        Text(
                            text = themeItem.styleCategory,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else themePalette.keySubtext,
                            fontSize = 8.5.sp
                        )
                    }
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
            if (res.isSuccess) {
                activeOutput = res.getOrNull()?.trim()
            }
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
            if (res.isSuccess) {
                activeOutput = res.getOrNull()?.trim()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
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
                Text("Writing Tone & Grammar Assistant", style = MaterialTheme.typography.titleSmall.copy(color = themePalette.keyText, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
        }

        // Tone quick pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(themePalette.accentPrimary)
                    .clickable { runFixGrammar() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Fix Grammar", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }

            ToneOption.entries.forEach { tone ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(themePalette.keyBackgroundPressed)
                        .clickable { runTone(tone) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(tone.label, color = themePalette.keyText, fontSize = 10.5.sp)
                }
            }
        }

        // Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(themePalette.keyBackground)
                .border(0.5.dp, themePalette.border.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            if (activeOutput != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(activeOutput ?: "", color = themePalette.keyText, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(themePalette.accentPrimary)
                            .clickable {
                                activeOutput?.let { onReplaceText(it, fetchedLength) }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Replace App Text", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text(
                    text = if (isProcessing) "Refining text with Gemini AI..." else "Tap 'Fix Grammar' or any Tone option to rephrase the active text in your app.",
                    color = themePalette.keySubtext,
                    fontSize = 11.5.sp
                )
            }
        }
    }
}
