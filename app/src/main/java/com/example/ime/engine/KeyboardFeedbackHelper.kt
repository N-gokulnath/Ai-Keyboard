package com.example.ime.engine

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class KeyboardFeedbackHelper(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_SYSTEM, 45)
    } catch (_: Exception) {
        null
    }

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun playKeyClick(soundEnabled: Boolean, effectType: Int = AudioManager.FX_KEYPRESS_STANDARD) {
        if (!soundEnabled) return
        var played = false
        try {
            audioManager?.playSoundEffect(effectType, 1.0f)
            played = true
        } catch (_: Exception) {}

        if (!played) {
            try {
                audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK)
                played = true
            } catch (_: Exception) {}
        }

        // Also trigger tone fallback if system sound effects are disabled in OS settings
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 18)
        } catch (_: Exception) {}
    }

    fun vibrate(hapticEnabled: Boolean, durationMs: Int = 15) {
        if (!hapticEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs.toLong().coerceIn(5, 50), VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs.toLong().coerceIn(5, 50))
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
