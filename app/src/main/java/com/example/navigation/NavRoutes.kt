package com.example.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object EnableKeyboard : Screen("enable_keyboard")
    object Home : Screen("home")
    object Compose : Screen("compose")
    object KeyboardDemo : Screen("keyboard_demo")
    object Themes : Screen("themes")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}
