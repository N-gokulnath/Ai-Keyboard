package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KeyboardRepository
import com.example.ime.engine.AospDictionary
import com.example.ime.engine.KeyboardLayouts
import com.example.model.KeyboardSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuraKeyboardEngineTest {

    @Test
    fun `dictionary provides prefix suggestions`() {
        val dict = AospDictionary()
        val suggestions = dict.getSuggestions("hel")
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.any { it.startsWith("hel", ignoreCase = true) })
    }

    @Test
    fun `keyboard layouts contain valid qwerty and symbol rows`() {
        assertEquals(10, KeyboardLayouts.QWERTY_ROW_1.size)
        assertEquals(9, KeyboardLayouts.QWERTY_ROW_2.size)
        assertEquals(7, KeyboardLayouts.QWERTY_ROW_3.size)
        assertTrue(KeyboardLayouts.EMOJI_CATEGORIES.containsKey("Smileys"))
    }

    @Test
    fun `keyboard repository saves and retrieves settings`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = KeyboardRepository.getInstance(context)

        val updated = KeyboardSettings(
            privateMode = true,
            selectedThemeId = "cyber_teal",
            hapticFeedback = true
        )
        repo.updateSettings(updated)

        val loaded = repo.getSettings()
        assertEquals("cyber_teal", loaded.selectedThemeId)
        assertTrue(loaded.privateMode)
    }
}
