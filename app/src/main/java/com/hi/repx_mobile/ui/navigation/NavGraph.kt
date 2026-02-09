package com.hi.repx_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hi.repx_mobile.viewmodel.RepXViewModel
import com.hi.repx_mobile.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: RepXViewModel
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Main screens
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToProfile = { navController.navigate(Screen.Home.route) }
            )
        }
        // Exercise screens
        // Routine screens
        // Progress screens
        // Profile
    }
}