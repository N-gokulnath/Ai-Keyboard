package com.example.ime

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.lifecycle.lifecycleScope
import com.example.MainActivity
import com.example.data.KeyboardRepository
import com.example.data.klipy.KlipyMediaItem
import com.example.ime.engine.AospDictionary
import com.example.ime.engine.KeyAction
import com.example.ime.engine.KeyboardFeedbackHelper
import com.example.ime.engine.KeyboardLayouts
import com.example.ime.engine.KeyboardMode
import com.example.ime.engine.RichContentHelper
import com.example.ime.engine.ShiftState
import com.example.ime.ui.AuraKeyboardView
import com.example.ime.ui.KeyboardThemePalette
import com.example.model.AIActionType
import com.example.model.KeyboardSettings
import com.example.ui.theme.MyApplicationTheme

class AuraInputMethodService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    private lateinit var repository: KeyboardRepository
    private lateinit var feedbackHelper: KeyboardFeedbackHelper
    private val dictionary = AospDictionary()

    private var shiftState by mutableStateOf(ShiftState.OFF)
    private var keyboardMode by mutableStateOf(KeyboardMode.ALPHA)
    private var currentSuggestions by mutableStateOf<List<String>>(emptyList())
    private var currentActionInfo by mutableStateOf(KeyAction(EditorInfo.IME_ACTION_UNSPECIFIED, "Return"))
    private var isSensitiveField by mutableStateOf(false)
    private var systemBottomInsetDp by mutableStateOf(0.dp)
    private var composingWord = StringBuilder()
    private var lastWord = ""

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        repository = KeyboardRepository.getInstance(this)
        feedbackHelper = KeyboardFeedbackHelper(this)

        configureImeWindow()
    }

    override fun onConfigureWindow(win: Window, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
        WindowCompat.setDecorFitsSystemWindows(win, false)
        win.navigationBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            win.isNavigationBarContrastEnforced = false
        }
    }

    private fun configureImeWindow() {
        window?.window?.let { win ->
            WindowCompat.setDecorFitsSystemWindows(win, false)
            win.navigationBarColor = android.graphics.Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                win.isNavigationBarContrastEnforced = false
            }
        }
    }

    private fun updateInsetsFromCompat(insets: WindowInsetsCompat?) {
        if (insets == null) return
        val density = resources.displayMetrics.density
        if (density <= 0f) return

        val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val tappable = insets.getInsets(WindowInsetsCompat.Type.tappableElement())
        val gestures = insets.getInsets(WindowInsetsCompat.Type.mandatorySystemGestures())

        val bottomPx = maxOf(navBars.bottom, sysBars.bottom, tappable.bottom, gestures.bottom)
        val calculatedDp = if (bottomPx > 0) (bottomPx / density).dp else 16.dp
        val finalDp = maxOf(calculatedDp, 16.dp)
        if (systemBottomInsetDp != finalDp) {
            systemBottomInsetDp = finalDp
        }
    }

    override fun onComputeInsets(outInsets: Insets) {
        super.onComputeInsets(outInsets)
        if (window?.window != null) {
            val decorView = window.window!!.decorView
            outInsets.contentTopInsets = decorView.height
            outInsets.visibleTopInsets = decorView.height
            outInsets.touchableInsets = Insets.TOUCHABLE_INSETS_FRAME
        }
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    private var initialAiActionType: AIActionType = AIActionType.COMPOSE

    override fun onCreateInputView(): View {
        configureImeWindow()

        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
        }

        val composeView = ComposeView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(this@AuraInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@AuraInputMethodService)
            setViewTreeViewModelStoreOwner(this@AuraInputMethodService)

            ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                updateInsetsFromCompat(insets)
                insets
            }

            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    val rootInsets = ViewCompat.getRootWindowInsets(v)
                    updateInsetsFromCompat(rootInsets)
                    v.requestApplyInsets()
                }
                override fun onViewDetachedFromWindow(v: View) {}
            })

            setContent {
                MyApplicationTheme {
                    val settings by repository.settingsFlow.collectAsState()
                    val profile by repository.profileFlow.collectAsState()
                    val themePalette = KeyboardThemePalette.getTheme(settings.selectedThemeId)

                    SideEffect {
                        try {
                            window?.window?.navigationBarColor = themePalette.background.toArgb()
                        } catch (_: Exception) {}
                    }

                    AuraKeyboardView(
                        themePalette = themePalette,
                        shiftState = shiftState,
                        keyboardMode = keyboardMode,
                        suggestions = currentSuggestions,
                        actionInfo = currentActionInfo,
                        isPrivateMode = settings.privateMode || isSensitiveField,
                        settings = settings,
                        profile = profile,
                        initialAiAction = initialAiActionType,
                        systemNavInsetDp = systemBottomInsetDp,
                        getCurrentInputText = { fetchCurrentText() },
                        onKeyChar = { char -> handleKeyChar(char, settings) },
                        onBackspace = { handleBackspace(settings) },
                        onSpace = { handleSpace(settings) },
                        onShiftToggle = { handleShiftToggle(settings) },
                        onModeChange = { newMode ->
                            keyboardMode = newMode
                            feedbackHelper.playKeyClick(settings.soundOnKeypress)
                            feedbackHelper.vibrate(settings.hapticFeedback, settings.vibrationDurationMs)
                        },
                        onActionClick = { handleActionClick(settings) },
                        onSuggestionClick = { suggestion -> handleSuggestionClick(suggestion, settings) },
                        onSwitchIme = { handleSwitchIme() },
                        onAiAction = { action -> handleAiAction(action) },
                        onCommitAiText = { text -> handleCommitAiText(text) },
                        onReplaceAiText = { text, chars -> handleReplaceAiText(text, chars) },
                        onOpenSettings = { handleOpenSettings() },
                        onThemeChange = { themeId ->
                            repository.updateSettings(settings.copy(selectedThemeId = themeId))
                        },
                        onToggleFloatingMode = {
                            repository.updateSettings(settings.copy(isFloatingMode = !settings.isFloatingMode))
                        },
                        onFloatingScaleChange = { scale ->
                            repository.updateSettings(settings.copy(floatingScale = scale))
                        },
                        onFloatingHeightScaleChange = { scale ->
                            repository.updateSettings(settings.copy(floatingHeightScale = scale))
                        },
                        onAddRecentEmoji = { emoji ->
                            val currentRecents = settings.recentEmojis.toMutableList()
                            currentRecents.remove(emoji)
                            currentRecents.add(0, emoji)
                            val updated = currentRecents.take(24)
                            repository.updateSettings(settings.copy(recentEmojis = updated))
                        },
                        onUpdatePinnedTools = { pinnedIds ->
                            repository.updateSettings(settings.copy(pinnedToolIds = pinnedIds))
                        },
                        onCommitRichContent = { uri, mimeType, desc, linkUri ->
                            handleCommitRichContent(uri, mimeType, desc, linkUri)
                        },
                        onCommitKlipyMedia = { mediaItem ->
                            RichContentHelper.commitKlipyMedia(
                                context = this@AuraInputMethodService,
                                scope = lifecycleScope,
                                inputConnection = currentInputConnection,
                                editorInfo = currentInputEditorInfo,
                                mediaItem = mediaItem
                            )
                        },
                        onDpadMove = { keyCode ->
                            feedbackHelper.playKeyClick(settings.soundOnKeypress)
                            feedbackHelper.vibrate(settings.hapticFeedback, settings.vibrationDurationMs)
                            sendDownUpKeyEvents(keyCode)
                        },
                        onSelectAll = {
                            currentInputConnection?.performContextMenuAction(android.R.id.selectAll)
                        },
                        onCopy = {
                            currentInputConnection?.performContextMenuAction(android.R.id.copy)
                        },
                        onCut = {
                            currentInputConnection?.performContextMenuAction(android.R.id.cut)
                        },
                        onPaste = {
                            currentInputConnection?.performContextMenuAction(android.R.id.paste)
                        },
                        onUndo = {
                            currentInputConnection?.performContextMenuAction(android.R.id.undo)
                        },
                        onHeightScaleChange = { scale ->
                            repository.updateSettings(settings.copy(keyboardHeightScale = scale))
                        },
                        onDismiss = {
                            requestHideSelf(0)
                        }
                    )
                }
            }
        }
        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        composingWord.clear()
        lastWord = ""
        keyboardMode = KeyboardMode.ALPHA

        if (info != null) {
            currentActionInfo = KeyboardLayouts.getActionInfo(info.imeOptions)

            val inputType = info.inputType
            val variation = inputType and InputType.TYPE_MASK_VARIATION
            val isPassword = variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                    variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                    variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                    variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD

            isSensitiveField = isPassword

            // Check auto-capitalization flags
            val capFlags = inputType and InputType.TYPE_MASK_FLAGS
            shiftState = if (capFlags and InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS != 0) {
                ShiftState.CAPS_LOCK
            } else if (capFlags and (InputType.TYPE_TEXT_FLAG_CAP_WORDS or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) != 0) {
                ShiftState.SHIFT
            } else {
                ShiftState.OFF
            }
        } else {
            isSensitiveField = false
            shiftState = ShiftState.SHIFT
        }

        updateSuggestions()
    }

    override fun onWindowShown() {
        super.onWindowShown()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        window?.window?.decorView?.let { decor ->
            val insets = ViewCompat.getRootWindowInsets(decor)
            updateInsetsFromCompat(insets)
            decor.requestApplyInsets()
        }
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        composingWord.clear()
        currentSuggestions = emptyList()
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && isInputViewShown) {
            requestHideSelf(0)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        feedbackHelper.release()
        super.onDestroy()
    }

    private fun handleKeyChar(char: String, settings: KeyboardSettings) {
        feedbackHelper.playKeyClick(settings.soundOnKeypress, AudioManager.FX_KEYPRESS_STANDARD)
        feedbackHelper.vibrate(settings.hapticFeedback, settings.vibrationDurationMs)
        val ic = currentInputConnection ?: return

        ic.commitText(char, 1)

        if (char.length == 1 && char[0].isLetter()) {
            composingWord.append(char)
            updateSuggestions()
        } else {
            if (char == "." || char == "!" || char == "?") {
                shiftState = ShiftState.SHIFT
            }
            composingWord.clear()
            updateSuggestions()
        }

        if (shiftState == ShiftState.SHIFT) {
            shiftState = ShiftState.OFF
        }
    }

    private fun handleBackspace(settings: KeyboardSettings) {
        feedbackHelper.playKeyClick(settings.soundOnKeypress, AudioManager.FX_KEYPRESS_DELETE)
        feedbackHelper.vibrate(settings.hapticFeedback, settings.vibrationDurationMs)
        val ic = currentInputConnection ?: return

        if (composingWord.isNotEmpty()) {
            composingWord.deleteCharAt(composingWord.length - 1)
            updateSuggestions()
            ic.deleteSurroundingText(1, 0)
            return
        }

        // Check text before cursor to safely delete surrogate pairs (emojis) and multi-code-point sequences
        val textBefore = ic.getTextBeforeCursor(16, 0)
        if (!textBefore.isNullOrEmpty()) {
            val len = textBefore.length
            val lastChar = textBefore[len - 1]

            // Check if last character is low surrogate (part of UTF-16 surrogate pair, like 😂, 🚀, 🥰)
            if (Character.isLowSurrogate(lastChar) && len >= 2 && Character.isHighSurrogate(textBefore[len - 2])) {
                var deleteCount = 2
                // Check if preceded by variation selector (e.g. \uFE0F) or skin tone / ZWJ
                if (len >= 3 && textBefore[len - 3] == '\uFE0F') {
                    deleteCount = 3
                }
                ic.deleteSurroundingText(deleteCount, 0)
            } else if (lastChar == '\uFE0F' && len >= 2) {
                if (len >= 3 && Character.isLowSurrogate(textBefore[len - 2]) && Character.isHighSurrogate(textBefore[len - 3])) {
                    ic.deleteSurroundingText(3, 0)
                } else {
                    ic.deleteSurroundingText(2, 0)
                }
            } else {
                ic.deleteSurroundingText(1, 0)
            }
        } else {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL)
        }
    }

    private fun handleSpace(settings: KeyboardSettings) {
        feedbackHelper.playKeyClick(settings.soundOnKeypress, AudioManager.FX_KEYPRESS_SPACEBAR)
        feedbackHelper.vibrate(settings.hapticFeedback, settings.vibrationDurationMs)
        val ic = currentInputConnection ?: return

        ic.commitText(" ", 1)
        if (composingWord.isNotEmpty()) {
            lastWord = composingWord.toString()
            composingWord.clear()
        }
        updateSuggestions()
    }

    private fun handleShiftToggle(settings: KeyboardSettings) {
        feedbackHelper.playKeyClick(settings.soundOnKeypress, AudioManager.FX_KEYPRESS_STANDARD)
        feedbackHelper.vibrate(settings.hapticFeedback, settings.vibrationDurationMs)
        shiftState = when (shiftState) {
            ShiftState.OFF -> ShiftState.SHIFT
            ShiftState.SHIFT -> ShiftState.CAPS_LOCK
            ShiftState.CAPS_LOCK -> ShiftState.OFF
        }
    }

    private fun handleSuggestionClick(suggestion: String, settings: KeyboardSettings) {
        feedbackHelper.playKeyClick(settings.soundOnKeypress, AudioManager.FX_KEYPRESS_STANDARD)
        feedbackHelper.vibrate(settings.hapticFeedback, settings.vibrationDurationMs)
        val ic = currentInputConnection ?: return

        if (composingWord.isNotEmpty()) {
            ic.deleteSurroundingText(composingWord.length, 0)
        }
        ic.commitText("$suggestion ", 1)
        lastWord = suggestion
        composingWord.clear()
        updateSuggestions()
    }

    private fun handleActionClick(settings: KeyboardSettings) {
        feedbackHelper.playKeyClick(settings.soundOnKeypress, AudioManager.FX_KEYPRESS_RETURN)
        feedbackHelper.vibrate(settings.hapticFeedback, settings.vibrationDurationMs)
        val ic = currentInputConnection ?: return

        if (currentActionInfo.actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(currentActionInfo.actionId)
        } else {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
        }
    }

    private fun handleSwitchIme() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                switchToNextInputMethod(false)
            } else {
                imm?.showInputMethodPicker()
            }
        } catch (_: Exception) {
            imm?.showInputMethodPicker()
        }
    }

    private fun handleAiAction(action: AIActionType) {
        // Open AI Studio INLINE inside keyboard without redirecting to app
        initialAiActionType = action
        keyboardMode = KeyboardMode.AI_STUDIO
        feedbackHelper.playKeyClick(true)
    }

    private fun handleCommitAiText(text: String) {
        val ic = currentInputConnection ?: return
        ic.commitText(text, 1)
        keyboardMode = KeyboardMode.ALPHA
    }

    private fun handleReplaceAiText(text: String, charsToReplace: Int) {
        val ic = currentInputConnection ?: return
        if (charsToReplace > 0) {
            ic.deleteSurroundingText(charsToReplace, 0)
        }
        ic.commitText(text, 1)
        keyboardMode = KeyboardMode.ALPHA
    }

    private fun fetchCurrentText(): String {
        val ic = currentInputConnection ?: return ""
        val selected = ic.getSelectedText(0)?.toString()
        if (!selected.isNullOrBlank()) return selected
        val before = ic.getTextBeforeCursor(400, 0)?.toString() ?: ""
        return before.trim()
    }

    private fun handleOpenSettings() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("NAVIGATE_TO", "settings")
        }
        startActivity(intent)
    }

    private fun handleCommitRichContent(
        contentUri: Uri,
        mimeType: String,
        description: String,
        linkUri: String?
    ): Boolean {
        val ic = currentInputConnection ?: return false
        val editorInfo = currentInputEditorInfo ?: return false

        val contentInfoCompat = InputContentInfoCompat(
            contentUri,
            ClipDescription(description, arrayOf(mimeType)),
            linkUri?.let { Uri.parse(it) }
        )

        var flags = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            flags = flags or InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
        }

        val supported = try {
            InputConnectionCompat.commitContent(
                ic,
                editorInfo,
                contentInfoCompat,
                flags,
                null
            )
        } catch (_: Exception) {
            false
        }

        if (!supported && linkUri != null) {
            ic.commitText(linkUri, 1)
            return true
        }

        return supported
    }

    private fun updateSuggestions() {
        val word = composingWord.toString()
        currentSuggestions = dictionary.getSuggestions(word, lastWord)
    }
}
