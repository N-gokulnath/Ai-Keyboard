package com.example.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.KeyboardRepository
import com.example.model.AIActionType
import com.example.navigation.Screen
import com.example.ui.screens.ComposeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KeyboardDemoScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ThemesScreen
import com.example.ui.screens.WelcomeScreen

@Composable
fun AppNavigation(
    initialRoute: String? = null,
    initialAiAction: AIActionType? = null
) {
    val context = LocalContext.current
    val repository = remember { KeyboardRepository.getInstance(context) }
    val navController = rememberNavController()

    val appSettings by repository.settingsFlow.collectAsState()
    val userProfile by repository.profileFlow.collectAsState()

    var activeInitialAction by remember { mutableStateOf(initialAiAction ?: AIActionType.COMPOSE) }
    var bufferKeyboardText by remember { mutableStateOf("") }

    val isOnboardingComplete = repository.isOnboardingCompleted()

    val startRoute = when (initialRoute) {
        "settings" -> Screen.Settings.route
        "themes" -> Screen.Themes.route
        "compose" -> Screen.Compose.route
        "home" -> Screen.Home.route
        "welcome" -> Screen.Welcome.route
        else -> if (isOnboardingComplete) Screen.Home.route else Screen.Welcome.route
    }

    LaunchedEffect(initialRoute, initialAiAction) {
        if (initialAiAction != null) {
            activeInitialAction = initialAiAction
        }
        if (initialRoute != null && initialRoute != "welcome") {
            val destination = when (initialRoute) {
                "settings" -> Screen.Settings.route
                "themes" -> Screen.Themes.route
                "compose" -> Screen.Compose.route
                "home" -> Screen.Home.route
                else -> Screen.Home.route
            }
            if (navController.currentDestination?.route != destination) {
                navController.navigate(destination) {
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startRoute,
        enterTransition = { fadeIn(animationSpec = tween(220)) },
        exitTransition = { fadeOut(animationSpec = tween(220)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
        popExitTransition = { fadeOut(animationSpec = tween(220)) }
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Screen.EnableKeyboard.route) },
                onDirectHome = {
                    repository.setOnboardingCompleted(true)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EnableKeyboard.route) {
            OnboardingScreen(
                onBack = { navController.popBackStack() },
                onContinue = {
                    repository.setOnboardingCompleted(true)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onTestKeyboard = {
                    repository.setOnboardingCompleted(true)
                    navController.navigate(Screen.KeyboardDemo.route)
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigate = { route -> navController.navigate(route) },
                onQuickAction = { action ->
                    activeInitialAction = action
                    navController.navigate(Screen.Compose.route)
                },
                onOpenProfile = { navController.navigate(Screen.Profile.route) },
                onOpenThemes = { navController.navigate(Screen.Themes.route) },
                onOpenTestKeyboard = { navController.navigate(Screen.KeyboardDemo.route) }
            )
        }

        composable(Screen.Compose.route) {
            ComposeScreen(
                initialAction = activeInitialAction,
                onNavigate = { route -> navController.navigate(route) },
                onInsertText = { inserted ->
                    bufferKeyboardText = inserted
                }
            )
        }

        composable(Screen.KeyboardDemo.route) {
            KeyboardDemoScreen(
                initialText = bufferKeyboardText,
                onBack = { navController.popBackStack() },
                onOpenFullCompose = { navController.navigate(Screen.Compose.route) }
            )
        }

        composable(Screen.Themes.route) {
            ThemesScreen(
                currentThemeId = appSettings.selectedThemeId,
                onSelectTheme = { newThemeId ->
                    repository.updateSettings(appSettings.copy(selectedThemeId = newThemeId))
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = appSettings,
                onUpdateSettings = { updated ->
                    repository.updateSettings(updated)
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                profile = userProfile,
                onSaveProfile = { updated ->
                    repository.updateProfile(updated)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

