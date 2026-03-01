package com.hi.repx_mobile.ui.navigation

import WorkoutHistoryScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.hi.repx_mobile.ui.screens.CreateRoutineScreen
import com.hi.repx_mobile.ui.screens.ExerciseSearchScreen
import com.hi.repx_mobile.ui.screens.HomeScreen
import com.hi.repx_mobile.ui.screens.LoginScreen
import com.hi.repx_mobile.ui.screens.ProfileScreen
import com.hi.repx_mobile.ui.screens.RegisterScreen
import com.hi.repx_mobile.ui.screens.RoutineListScreen
import com.hi.repx_mobile.ui.screens.WorkoutDetailScreen
import com.hi.repx_mobile.ui.screens.WorkoutScreen

import com.hi.repx_mobile.viewmodel.RepXViewModel

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
                onStartWorkout = { workoutId ->
                    navController.navigate(Screen.Workout.createRoute(workoutId))
                },
                onNavigateToHistory = { navController.navigate(Screen.WorkoutHistory.route) },
                onNavigateToRoutines = { navController.navigate(Screen.RoutineList.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(
            route = Screen.Workout.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
            WorkoutScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onAddExercise = { navController.navigate(Screen.ExerciseSearch.createRoute(workoutId)) },
                onFinishWorkout = { navController.popBackStack() }
            )
        }

        composable(Screen.WorkoutHistory.route) {
            WorkoutHistoryScreen(
                viewModel = viewModel,
                onWorkoutClick = { workoutId ->
                    navController.navigate(Screen.WorkoutDetail.createRoute(workoutId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
            WorkoutDetailScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onCopyWorkout = { copiedWorkoutId ->
                    navController.navigate(Screen.Workout.createRoute(copiedWorkoutId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Exercise search
        composable(
            route = Screen.ExerciseSearch.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
            ExerciseSearchScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onExerciseAdded = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // Routine screens
        composable(Screen.RoutineList.route) {
            RoutineListScreen(
                viewModel = viewModel,
                onCreateRoutine = { navController.navigate(Screen.CreateRoutine.route) },
                onStartWorkoutFromRoutine = { workoutId ->
                    navController.navigate(Screen.Workout.createRoute(workoutId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateRoutine.route) {
            CreateRoutineScreen(
                viewModel = viewModel,
                onRoutineCreated = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
