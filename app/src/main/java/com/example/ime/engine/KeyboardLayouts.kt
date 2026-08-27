package com.example.ime.engine

import android.view.inputmethod.EditorInfo

enum class ShiftState {
    OFF,
    SHIFT,
    CAPS_LOCK
}

enum class KeyboardMode {
    ALPHA,
    NORMAL_MODE,
    SYMBOLS,
    MORE_SYMBOLS,
    EMOJI,
    EMOJI_SEARCH_MODE,
    GIF,
    GIF_SEARCH_MODE,
    STICKERS,
    STICKER_MODE,
    AI_STUDIO,
    AI_CHAT_MODE,
    TOOLBAR_CUSTOMIZATION_MODE,
    CLIPBOARD,
    TRANSLATION,
    WRITING_ASSISTANT,
    TEXT_EDITING,
    THEMES
}

data class KeyAction(
    val actionId: Int,
    val label: String
)

object KeyboardLayouts {

    val QWERTY_ROW_1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val QWERTY_ROW_2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val QWERTY_ROW_3 = listOf("z", "x", "c", "v", "b", "n", "m")

    val NUMBERS_ROW = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val SYMBOLS_ROW_1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val SYMBOLS_ROW_2 = listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/")
    val SYMBOLS_ROW_3 = listOf("*", "\"", "'", ":", ";", "!", "?")

    val MORE_SYMBOLS_ROW_1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    val MORE_SYMBOLS_ROW_2 = listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\")
    val MORE_SYMBOLS_ROW_3 = listOf("%", "©", "®", "™", "✓", "[", "]")

    val EMOJI_CATEGORIES = mapOf(
        "Smileys" to listOf(
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥹", "😊",
            "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙",
            "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔",
            "🫡", "🤐", "🤨", "😐", "😑", "😶", "🫥", "😏", "😒", "🙄",
            "😬", "😮‍💨", "🤥", "😔", "😪", "🤤", "😴", "😷", "🤒", "😎"
        ),
        "Hands" to listOf(
            "👋", "🤚", "🖐️", "✋", "🖖", "🫱", "🫲", "🫸", "🫷", "👌",
            "🤌", "🤏", "✌️", "🤞", "🫰", "🤟", "🤘", "🤙", "👈", "👉",
            "👆", "🖕", "👇", "☝️", "👍", "👎", "✊", "👊", "🤛", "🤜",
            "👏", "🙌", "🫶", "👐", "🤲", "🤝", "🙏"
        ),
        "Hearts" to listOf(
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝",
            "✨", "⭐", "🌟", "💫", "🔥", "💥", "🎉", "🎊"
        ),
        "Work & Tech" to listOf(
            "💻", "📱", "⌨️", "🖥️", "🖨️", "🖱️", "📷", "💡", "📝", "📊",
            "📈", "📉", "📁", "📂", "📌", "📍", "📎", "💼", "✉️", "📧",
            "🚀", "⚡", "🔮", "🎯", "🏆", "🥇", "🔒", "🔑"
        ),
        "Food & Fun" to listOf(
            "☕", "🍵", "🍕", "🍔", "🍟", "🌮", "🍣", "🍦", "🍩", "🍰",
            "🍺", "🍻", "🥂", "🍷", "🍾", "🍿", "🎮", "⚽", "🏀", "🎨"
        ),
        "Animals" to listOf(
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
            "🦁", "🐮", "🐷", "🐸", "🐵", "🦄", "🦅", "🦉", "🦋", "🐝"
        )
    )

    val EMOJI_KEYWORD_TAGS = mapOf(
        "happy" to listOf("😀", "😃", "😄", "😁", "😆", "😊", "🙂", "🥳", "😎"),
        "smile" to listOf("😀", "😃", "😄", "😁", "😊", "🙂", "😉"),
        "laugh" to listOf("😂", "🤣", "😆", "😅", "😄"),
        "love" to listOf("❤️", "🥰", "😍", "😘", "💖", "💕", "🫶", "💘", "💓"),
        "heart" to listOf("❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❤️‍🔥", "💖", "💕"),
        "fire" to listOf("🔥", "💥", "✨", "🚀", "⚡"),
        "cool" to listOf("😎", "🤘", "🤙", "🔥", "✨", "🚀"),
        "sad" to listOf("😔", "😪", "😢", "😭", "🥺", "💔", "😞"),
        "cry" to listOf("😂", "🤣", "😭", "😢", "🥹"),
        "hand" to listOf("👋", "👍", "👎", "👌", "✌️", "🤞", "👏", "🙌", "🤝", "🙏"),
        "thumbs" to listOf("👍", "👎"),
        "party" to listOf("🎉", "🎊", "🥳", "🍾", "🥂", "🍕"),
        "work" to listOf("💻", "💼", "📊", "📈", "📝", "📁", "✉️", "📧"),
        "tech" to listOf("💻", "📱", "⌨️", "🖥️", "🖱️", "💡", "🚀", "⚡", "🔮"),
        "food" to listOf("☕", "🍕", "🍔", "🍟", "🌮", "🍣", "🍦", "🍩", "🍰", "🍿"),
        "drink" to listOf("☕", "🍵", "🍺", "🍻", "🥂", "🍷", "🍾"),
        "dog" to listOf("🐶", "🦮", "🐕"),
        "cat" to listOf("🐱", "🐈", "🦁", "🐯"),
        "star" to listOf("⭐", "🌟", "✨", "💫"),
        "check" to listOf("✓", "✔️", "✅", "👍"),
        "ok" to listOf("👌", "👍", "✓", "💯")
    )

    fun searchEmojis(query: String): List<String> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return EMOJI_CATEGORIES.values.flatten().distinct()

        val results = mutableListOf<String>()
        // Exact or prefix tag match
        EMOJI_KEYWORD_TAGS.forEach { (tag, emojis) ->
            if (tag.contains(q) || q.contains(tag)) {
                results.addAll(emojis)
            }
        }
        // Category names match
        EMOJI_CATEGORIES.forEach { (category, emojis) ->
            if (category.lowercase().contains(q)) {
                results.addAll(emojis)
            }
        }
        return if (results.isNotEmpty()) results.distinct() else EMOJI_CATEGORIES.values.flatten().distinct()
    }

    fun getActionInfo(imeOptions: Int): KeyAction {
        val action = imeOptions and EditorInfo.IME_MASK_ACTION
        return when (action) {
            EditorInfo.IME_ACTION_GO -> KeyAction(action, "Go")
            EditorInfo.IME_ACTION_SEARCH -> KeyAction(action, "Search")
            EditorInfo.IME_ACTION_SEND -> KeyAction(action, "Send")
            EditorInfo.IME_ACTION_NEXT -> KeyAction(action, "Next")
            EditorInfo.IME_ACTION_DONE -> KeyAction(action, "Done")
            else -> KeyAction(EditorInfo.IME_ACTION_UNSPECIFIED, "Return")
        }
    }
}
