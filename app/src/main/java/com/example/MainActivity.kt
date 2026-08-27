package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.KeyboardRepository
import com.example.model.AIActionType
import com.example.ui.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private var initialDestination by mutableStateOf<String?>(null)
    private var initialAiAction by mutableStateOf<AIActionType?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingIntent(intent)

        setContent {
            val repository = remember { KeyboardRepository.getInstance(this@MainActivity) }
            val settings by repository.settingsFlow.collectAsState()

            val isDark = when (settings.appThemeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark) {
                AppNavigation(
                    initialRoute = initialDestination,
                    initialAiAction = initialAiAction
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val navTo = intent?.getStringExtra("NAVIGATE_TO")
        if (navTo != null) {
            initialDestination = navTo
        }

        val aiActionStr = intent?.getStringExtra("AI_ACTION")
        if (aiActionStr != null) {
            try {
                initialAiAction = AIActionType.valueOf(aiActionStr)
                initialDestination = "compose"
            } catch (_: Exception) {}
        }
    }
}
