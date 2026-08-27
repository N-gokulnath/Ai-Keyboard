package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import com.example.model.AIProcessingMode
import com.example.model.KeyboardSettings
import com.example.model.PreferredStyle
import com.example.model.WritingProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeyboardRepository private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<KeyboardSettings> = _settingsFlow.asStateFlow()

    private val _profileFlow = MutableStateFlow(loadProfile())
    val profileFlow: StateFlow<WritingProfile> = _profileFlow.asStateFlow()

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun addRecentEmoji(emoji: String) {
        val current = _settingsFlow.value.recentEmojis.toMutableList()
        current.remove(emoji)
        current.add(0, emoji)
        val limited = current.take(24)
        val updated = _settingsFlow.value.copy(recentEmojis = limited)
        val serialized = limited.joinToString(",")
        prefs.edit().putString(KEY_RECENT_EMOJIS, serialized).apply()
        _settingsFlow.value = updated
    }

    fun getSettings(): KeyboardSettings = _settingsFlow.value

    fun updateSettings(settings: KeyboardSettings) {
        prefs.edit()
            .putString(KEY_APP_THEME_MODE, settings.appThemeMode)
            .putBoolean(KEY_PRIVATE_MODE, settings.privateMode)
            .putString(KEY_PROCESSING_MODE, settings.processingMode.name)
            .putBoolean(KEY_SENSITIVE_PROTECTION, settings.sensitiveProtection)
            .putString(KEY_SELECTED_THEME_ID, settings.selectedThemeId)
            .putString(KEY_AI_MODEL, settings.aiModel)
            .putBoolean(KEY_AUTO_CAPITALIZATION, settings.autoCapitalization)
            .putBoolean(KEY_DOUBLE_SPACE_PERIOD, settings.doubleSpacePeriod)
            .putBoolean(KEY_HAPTIC_FEEDBACK, settings.hapticFeedback)
            .putInt(KEY_VIBRATION_DURATION, settings.vibrationDurationMs)
            .putBoolean(KEY_SOUND_ON_KEYPRESS, settings.soundOnKeypress)
            .putBoolean(KEY_POPUP_ON_KEYPRESS, settings.popupOnKeypress)
            .putBoolean(KEY_SHOW_NUMBER_ROW, settings.showNumberRow)
            .putBoolean(KEY_SHOW_LANGUAGE_SWITCH_KEY, settings.showLanguageSwitchKey)
            .putBoolean(KEY_SHOW_EMOJI_KEY, settings.showEmojiKey)
            .putFloat(KEY_KEYBOARD_HEIGHT_SCALE, settings.keyboardHeightScale)
            .putInt(KEY_BOTTOM_INSET_PADDING, settings.bottomInsetPaddingDp)
            .putBoolean(KEY_AUTO_CORRECTION, settings.autoCorrection)
            .putBoolean(KEY_SHOW_SUGGESTIONS, settings.showSuggestions)
            .putBoolean(KEY_NEXT_WORD_SUGGESTIONS, settings.nextWordSuggestions)
            .putBoolean(KEY_BLOCK_OFFENSIVE_WORDS, settings.blockOffensiveWords)
            .putBoolean(KEY_AUTO_SPACE_PUNCTUATION, settings.autoSpaceAfterPunctuation)
            .putBoolean(KEY_IS_FLOATING_MODE, settings.isFloatingMode)
            .putFloat(KEY_FLOATING_SCALE, settings.floatingScale)
            .putFloat(KEY_FLOATING_HEIGHT_SCALE, settings.floatingHeightScale)
            .putFloat(KEY_FLOATING_OFFSET_X, settings.floatingOffsetX)
            .putFloat(KEY_FLOATING_OFFSET_Y, settings.floatingOffsetY)
            .putString(KEY_RECENT_EMOJIS, settings.recentEmojis.joinToString(","))
            .putString(KEY_PINNED_TOOLS, settings.pinnedToolIds.joinToString(","))
            .apply()
        _settingsFlow.value = settings
    }

    fun getProfile(): WritingProfile = _profileFlow.value

    fun updateProfile(profile: WritingProfile) {
        prefs.edit()
            .putString(KEY_USER_NAME, profile.name)
            .putString(KEY_USER_PROFESSION, profile.profession)
            .putString(KEY_USER_STYLE, profile.style.name)
            .putStringSet(KEY_USER_TONES, profile.tones)
            .apply()
        _profileFlow.value = profile
    }

    private fun loadSettings(): KeyboardSettings {
        val appThemeMode = prefs.getString(KEY_APP_THEME_MODE, "system") ?: "system"
        val privateMode = prefs.getBoolean(KEY_PRIVATE_MODE, true)
        val processingModeStr = prefs.getString(KEY_PROCESSING_MODE, AIProcessingMode.CLOUD_ASSIST.name)
        val processingMode = try {
            AIProcessingMode.valueOf(processingModeStr ?: AIProcessingMode.CLOUD_ASSIST.name)
        } catch (_: Exception) {
            AIProcessingMode.CLOUD_ASSIST
        }
        val sensitiveProtection = prefs.getBoolean(KEY_SENSITIVE_PROTECTION, true)
        val selectedThemeId = prefs.getString(KEY_SELECTED_THEME_ID, "dynamic") ?: "dynamic"
        val aiModel = prefs.getString(KEY_AI_MODEL, "Gemini 2.5 Flash (On-Device + Cloud)") ?: "Gemini 2.5 Flash (On-Device + Cloud)"

        val autoCapitalization = prefs.getBoolean(KEY_AUTO_CAPITALIZATION, true)
        val doubleSpacePeriod = prefs.getBoolean(KEY_DOUBLE_SPACE_PERIOD, true)
        val hapticFeedback = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true)
        val vibrationDurationMs = prefs.getInt(KEY_VIBRATION_DURATION, 15)
        val soundOnKeypress = prefs.getBoolean(KEY_SOUND_ON_KEYPRESS, false)
        val popupOnKeypress = prefs.getBoolean(KEY_POPUP_ON_KEYPRESS, true)
        val showNumberRow = prefs.getBoolean(KEY_SHOW_NUMBER_ROW, false)
        val showLanguageSwitchKey = prefs.getBoolean(KEY_SHOW_LANGUAGE_SWITCH_KEY, false)
        val showEmojiKey = prefs.getBoolean(KEY_SHOW_EMOJI_KEY, true)
        val keyboardHeightScale = prefs.getFloat(KEY_KEYBOARD_HEIGHT_SCALE, 1.0f)
        val bottomInsetPaddingDp = prefs.getInt(KEY_BOTTOM_INSET_PADDING, 28)

        val autoCorrection = prefs.getBoolean(KEY_AUTO_CORRECTION, true)
        val showSuggestions = prefs.getBoolean(KEY_SHOW_SUGGESTIONS, true)
        val nextWordSuggestions = prefs.getBoolean(KEY_NEXT_WORD_SUGGESTIONS, true)
        val blockOffensiveWords = prefs.getBoolean(KEY_BLOCK_OFFENSIVE_WORDS, true)
        val autoSpaceAfterPunctuation = prefs.getBoolean(KEY_AUTO_SPACE_PUNCTUATION, true)
        val isFloatingMode = prefs.getBoolean(KEY_IS_FLOATING_MODE, false)
        val floatingScale = prefs.getFloat(KEY_FLOATING_SCALE, 0.88f)
        val floatingHeightScale = prefs.getFloat(KEY_FLOATING_HEIGHT_SCALE, 1.0f)
        val floatingOffsetX = prefs.getFloat(KEY_FLOATING_OFFSET_X, 0f)
        val floatingOffsetY = prefs.getFloat(KEY_FLOATING_OFFSET_Y, 0f)
        val recentEmojisRaw = prefs.getString(KEY_RECENT_EMOJIS, "😀,❤️,🔥,👍,✨,🚀,🎉,🙏") ?: "😀,❤️,🔥,👍,✨,🚀,🎉,🙏"
        val recentEmojis = recentEmojisRaw.split(",").filter { it.isNotBlank() }
        val pinnedToolsRaw = prefs.getString(KEY_PINNED_TOOLS, "emoji,gif,stickers,ai_studio") ?: "emoji,gif,stickers,ai_studio"
        val pinnedToolIds = pinnedToolsRaw.split(",").filter { it.isNotBlank() }

        return KeyboardSettings(
            appThemeMode = appThemeMode,
            privateMode = privateMode,
            processingMode = processingMode,
            sensitiveProtection = sensitiveProtection,
            selectedThemeId = selectedThemeId,
            aiModel = aiModel,
            autoCapitalization = autoCapitalization,
            doubleSpacePeriod = doubleSpacePeriod,
            hapticFeedback = hapticFeedback,
            vibrationDurationMs = vibrationDurationMs,
            soundOnKeypress = soundOnKeypress,
            popupOnKeypress = popupOnKeypress,
            showNumberRow = showNumberRow,
            showLanguageSwitchKey = showLanguageSwitchKey,
            showEmojiKey = showEmojiKey,
            keyboardHeightScale = keyboardHeightScale,
            bottomInsetPaddingDp = bottomInsetPaddingDp,
            autoCorrection = autoCorrection,
            showSuggestions = showSuggestions,
            nextWordSuggestions = nextWordSuggestions,
            blockOffensiveWords = blockOffensiveWords,
            autoSpaceAfterPunctuation = autoSpaceAfterPunctuation,
            isFloatingMode = isFloatingMode,
            floatingScale = floatingScale,
            floatingHeightScale = floatingHeightScale,
            floatingOffsetX = floatingOffsetX,
            floatingOffsetY = floatingOffsetY,
            recentEmojis = recentEmojis,
            pinnedToolIds = pinnedToolIds
        )
    }

    private fun loadProfile(): WritingProfile {
        val name = prefs.getString(KEY_USER_NAME, "Alex Rivera") ?: "Alex Rivera"
        val profession = prefs.getString(KEY_USER_PROFESSION, "Product Designer") ?: "Product Designer"
        val styleStr = prefs.getString(KEY_USER_STYLE, PreferredStyle.NATURAL.name)
        val style = try {
            PreferredStyle.valueOf(styleStr ?: PreferredStyle.NATURAL.name)
        } catch (_: Exception) {
            PreferredStyle.NATURAL
        }
        val tones = prefs.getStringSet(KEY_USER_TONES, setOf("Confident", "Empathetic")) ?: setOf("Confident", "Empathetic")

        return WritingProfile(
            name = name,
            profession = profession,
            style = style,
            tones = tones
        )
    }

    fun isImeEnabled(): Boolean {
        return try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val enabledMethods = imm?.enabledInputMethodList ?: emptyList()
            val packageName = context.packageName
            enabledMethods.any { it.packageName == packageName }
        } catch (_: Exception) {
            false
        }
    }

    fun isImeSelected(): Boolean {
        return try {
            val currentIme = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            )
            currentIme?.contains(context.packageName) == true
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        private const val PREFS_NAME = "aura_keyboard_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_APP_THEME_MODE = "app_theme_mode"
        private const val KEY_PRIVATE_MODE = "private_mode"
        private const val KEY_PROCESSING_MODE = "processing_mode"
        private const val KEY_SENSITIVE_PROTECTION = "sensitive_protection"
        private const val KEY_SELECTED_THEME_ID = "selected_theme_id"
        private const val KEY_AI_MODEL = "ai_model"

        private const val KEY_AUTO_CAPITALIZATION = "auto_capitalization"
        private const val KEY_DOUBLE_SPACE_PERIOD = "double_space_period"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        private const val KEY_VIBRATION_DURATION = "vibration_duration"
        private const val KEY_SOUND_ON_KEYPRESS = "sound_on_keypress"
        private const val KEY_POPUP_ON_KEYPRESS = "popup_on_keypress"
        private const val KEY_SHOW_NUMBER_ROW = "show_number_row"
        private const val KEY_SHOW_LANGUAGE_SWITCH_KEY = "show_language_switch_key"
        private const val KEY_SHOW_EMOJI_KEY = "show_emoji_key"
        private const val KEY_KEYBOARD_HEIGHT_SCALE = "keyboard_height_scale"
        private const val KEY_BOTTOM_INSET_PADDING = "bottom_inset_padding"

        private const val KEY_AUTO_CORRECTION = "auto_correction"
        private const val KEY_SHOW_SUGGESTIONS = "show_suggestions"
        private const val KEY_NEXT_WORD_SUGGESTIONS = "next_word_suggestions"
        private const val KEY_BLOCK_OFFENSIVE_WORDS = "block_offensive_words"
        private const val KEY_AUTO_SPACE_PUNCTUATION = "auto_space_punctuation"
        private const val KEY_IS_FLOATING_MODE = "is_floating_mode"
        private const val KEY_FLOATING_SCALE = "floating_scale"
        private const val KEY_FLOATING_HEIGHT_SCALE = "floating_height_scale"
        private const val KEY_FLOATING_OFFSET_X = "floating_offset_x"
        private const val KEY_FLOATING_OFFSET_Y = "floating_offset_y"
        private const val KEY_RECENT_EMOJIS = "recent_emojis"
        private const val KEY_PINNED_TOOLS = "pinned_tools"

        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PROFESSION = "user_profession"
        private const val KEY_USER_STYLE = "user_style"
        private const val KEY_USER_TONES = "user_tones"

        @Volatile
        private var INSTANCE: KeyboardRepository? = null

        fun getInstance(context: Context): KeyboardRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: KeyboardRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
