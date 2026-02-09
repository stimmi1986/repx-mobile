package com.hi.repx_mobile.ui.navigation

sealed class Screen(val route: String) {
    // Auth screens
    object Login : Screen("login")
    object Register : Screen("register")

    // Main screens
    object Home : Screen("home")
    // Exercise screens
    // Routine screens
    // Progress screens
    // Settings
}
