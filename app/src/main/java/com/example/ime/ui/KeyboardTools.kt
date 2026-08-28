package com.example.ime.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ime.engine.KeyboardMode

data class KeyboardToolItem(
    val id: String,
    val title: String,
    val shortName: String,
    val icon: ImageVector,
    val targetMode: KeyboardMode,
    val description: String
)

object KeyboardTools {
    val TOOL_EMOJI = KeyboardToolItem(
        id = "emoji",
        title = "Emoji Search",
        shortName = "Emojis",
        icon = Icons.Default.SentimentSatisfied,
        targetMode = KeyboardMode.EMOJI,
        description = "Search and insert expressive emojis"
    )

    val TOOL_GIF = KeyboardToolItem(
        id = "gif",
        title = "GIF Search",
        shortName = "GIFs",
        icon = Icons.Default.Gif,
        targetMode = KeyboardMode.GIF,
        description = "Find and send animated reaction GIFs"
    )

    val TOOL_STICKERS = KeyboardToolItem(
        id = "stickers",
        title = "Stickers",
        shortName = "Stickers",
        icon = Icons.Default.StickyNote2,
        targetMode = KeyboardMode.STICKERS,
        description = "Browse high-res stickers and custom art"
    )

    val TOOL_AI_STUDIO = KeyboardToolItem(
        id = "ai_studio",
        title = "AI Studio",
        shortName = "AI Assist",
        icon = Icons.Default.AutoAwesome,
        targetMode = KeyboardMode.AI_STUDIO,
        description = "Draft, reply, rewrite & summarize with Gemini"
    )

    val TOOL_CLIPBOARD = KeyboardToolItem(
        id = "clipboard",
        title = "Clipboard",
        shortName = "Clipboard",
        icon = Icons.Default.ContentPaste,
        targetMode = KeyboardMode.CLIPBOARD,
        description = "Access recent copied text clips"
    )

    val TOOL_WRITING_ASSISTANT = KeyboardToolItem(
        id = "writing_assistant",
        title = "Tone & Text",
        shortName = "Tone/Text",
        icon = Icons.Default.FormatSize,
        targetMode = KeyboardMode.WRITING_ASSISTANT,
        description = "Fix grammar and adjust phrasing tone"
    )

    val TOOL_THEMES = KeyboardToolItem(
        id = "themes",
        title = "Themes",
        shortName = "Themes",
        icon = Icons.Default.Palette,
        targetMode = KeyboardMode.THEMES,
        description = "Customize keyboard appearance and glows"
    )

    val TOOL_TRANSLATE = KeyboardToolItem(
        id = "translate",
        title = "Translation",
        shortName = "Translate",
        icon = Icons.Default.Translate,
        targetMode = KeyboardMode.TRANSLATION,
        description = "Live translate input between languages"
    )

    val TOOL_TEXT_EDITING = KeyboardToolItem(
        id = "text_editing",
        title = "Text Editing",
        shortName = "Cursor Pad",
        icon = Icons.Default.TextFields,
        targetMode = KeyboardMode.TEXT_EDITING,
        description = "Precision cursor navigation and selection"
    )

    val TOOL_FLOATING = KeyboardToolItem(
        id = "floating",
        title = "Floating Mode",
        shortName = "Floating",
        icon = Icons.Default.PictureInPicture,
        targetMode = KeyboardMode.NORMAL_MODE, // Special toggle action
        description = "Detach keyboard and move freely on screen"
    )

    val TOOL_SETTINGS = KeyboardToolItem(
        id = "settings",
        title = "Settings",
        shortName = "Settings",
        icon = Icons.Default.Settings,
        targetMode = KeyboardMode.NORMAL_MODE, // Special intent action
        description = "Configure haptics, sounds, and preferences"
    )

    val TOOL_CUSTOMIZE = KeyboardToolItem(
        id = "customize",
        title = "Customize Toolbar",
        shortName = "Customize",
        icon = Icons.Default.Edit,
        targetMode = KeyboardMode.TOOLBAR_CUSTOMIZATION_MODE,
        description = "Pin, unpin, and organize toolbar tools"
    )

    val ALL_TOOLS: List<KeyboardToolItem> = listOf(
        TOOL_CLIPBOARD,
        TOOL_WRITING_ASSISTANT,
        TOOL_THEMES,
        TOOL_TRANSLATE,
        TOOL_EMOJI,
        TOOL_GIF,
        TOOL_STICKERS,
        TOOL_AI_STUDIO,
        TOOL_TEXT_EDITING,
        TOOL_FLOATING,
        TOOL_SETTINGS,
        TOOL_CUSTOMIZE
    )

    val DEFAULT_PINNED_IDS: List<String> = listOf("clipboard", "writing_assistant", "themes", "translate", "emoji")

    fun getToolById(id: String): KeyboardToolItem? {
        return ALL_TOOLS.find { it.id == id }
    }
}
