package com.example.russianpath.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.russianpath.presentation.screens.dashboard.DashboardScreen
import com.example.russianpath.presentation.screens.lesson.LessonScreen
import com.example.russianpath.presentation.screens.result.ResultScreen
import com.example.russianpath.presentation.screens.profile.ProfileScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")
    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
    object Result : Screen("result/{lessonId}/{stars}/{xp}") {
        fun createRoute(lessonId: String, stars: Int, xp: Int) =
            "result/$lessonId/$stars/$xp"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {

        // Главный экран
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(lessonId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Экран урока
        composable(
            route = Screen.Lesson.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable

            LessonScreen(
                onBackClick = { navController.popBackStack() },
                onComplete = {
                    navController.navigate(
                        Screen.Result.createRoute(lessonId, stars = 3, xp = 100)
                    ) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        // Экран результата
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("stars") { type = NavType.IntType },
                navArgument("xp") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val stars = backStackEntry.arguments?.getInt("stars") ?: 0
            val xp = backStackEntry.arguments?.getInt("xp") ?: 0

            ResultScreen(
                onContinue = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onRepeat = {
                    navController.popBackStack()
                }
            )
        }

        // Экран профиля
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
