package com.example.model

enum class ToneOption(val label: String) {
    PROFESSIONAL("Professional"),
    FRIENDLY("Friendly"),
    CONCISE("Concise"),
    CONFIDENT("Confident"),
    PERSUASIVE("Persuasive"),
    CASUAL("Casual")
}

enum class AIActionType(val title: String, val iconName: String, val promptHint: String) {
    COMPOSE("Draft", "edit_note", "What do you want to write?"),
    REWRITE("Rewrite", "history_edu", "Paste or type text to rephrase with higher impact..."),
    REPLY("Reply", "reply", "Paste message to draft an intelligent response..."),
    FIX("Fix Grammar", "spellcheck", "Paste text to fix typos, grammar, and phrasing..."),
    TRANSLATE("Translate", "translate", "Enter text to translate seamlessly..."),
    SUMMARIZE("Summarize", "summarize", "Enter lengthy text to distill into key bullet points...")
}

data class ThemeItem(
    val id: String,
    val name: String,
    val description: String,
    val primaryColorHex: Long,
    val surfaceColorHex: Long,
    val accentColorHex: Long,
    val isDark: Boolean = true
)

enum class PreferredStyle(val title: String, val description: String) {
    CONCISE("Concise", "Direct & to the point"),
    NATURAL("Natural", "Conversational & clear"),
    FORMAL("Formal", "Professional & structured")
}

data class WritingProfile(
    val name: String = "Alex Rivera",
    val profession: String = "Product Designer",
    val style: PreferredStyle = PreferredStyle.NATURAL,
    val tones: Set<String> = setOf("Confident", "Empathetic")
)

enum class AIProcessingMode(val label: String) {
    LOCAL_ONLY("Local Only"),
    CLOUD_ASSIST("Cloud Assist")
}

data class AiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class KeyboardSettings(
    // App & Interface Appearance
    val appThemeMode: String = "system", // "system", "dark", "light"

    // Privacy & AI
    val privateMode: Boolean = true,
    val processingMode: AIProcessingMode = AIProcessingMode.CLOUD_ASSIST,
    val sensitiveProtection: Boolean = true,
    val selectedThemeId: String = "dynamic",
    val aiModel: String = "Gemini 2.5 Flash (On-Device + Cloud)",

    // AOSP Typing & Preferences
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
    val hapticFeedback: Boolean = true,
    val vibrationDurationMs: Int = 15,
    val soundOnKeypress: Boolean = false,
    val popupOnKeypress: Boolean = true,
    val showNumberRow: Boolean = false,
    val showLanguageSwitchKey: Boolean = false,
    val showEmojiKey: Boolean = true,
    val keyboardHeightScale: Float = 1.0f,
    val bottomInsetPaddingDp: Int = 28,
    val isFloatingMode: Boolean = false,
    val floatingScale: Float = 0.88f,
    val floatingHeightScale: Float = 1.0f,
    val floatingOffsetX: Float = 0f,
    val floatingOffsetY: Float = 0f,
    val recentEmojis: List<String> = listOf("😀", "❤️", "🔥", "👍", "✨", "🚀", "🎉", "🙏"),
    val pinnedToolIds: List<String> = listOf("emoji", "gif", "stickers", "ai_studio"),

    // AOSP Text Correction & Suggestions
    val autoCorrection: Boolean = true,
    val showSuggestions: Boolean = true,
    val nextWordSuggestions: Boolean = true,
    val blockOffensiveWords: Boolean = true,
    val autoSpaceAfterPunctuation: Boolean = true
)
