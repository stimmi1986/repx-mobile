package com.hi.repx_mobile.ui.navigation

sealed class Screen(val route: String) {
    // Auth screens
    object Login : Screen("login")
    object Register : Screen("register")

    // Main screens
    object Home : Screen("home")
    object Workout : Screen("workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout/$workoutId"
    }
    object WorkoutHistory : Screen("workout_history")
    object WorkoutDetail : Screen("workout_detail/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout_detail/$workoutId"
    }

    // Exercise screens
    object ExerciseSearch : Screen("exercise_search/{workoutId}") {
        fun createRoute(workoutId: Long) = "exercise_search/$workoutId"
    }

    // Routine screens
    object RoutineList : Screen("routines")
    object CreateRoutine : Screen("create_routine")

    // Progress screens
    object ProgressPhotos : Screen("progress_photos")

    // Body Weight screens
    object BodyWeight : Screen("body_weight")

    // Settings
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
