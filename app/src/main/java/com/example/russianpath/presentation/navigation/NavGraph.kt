// app/src/main/java/com/example/russianpath/presentation/navigation/NavGraph.kt

package com.example.russianpath.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.russianpath.presentation.screens.dashboard.DashboardScreen
import com.example.russianpath.presentation.screens.dashboard.DashboardViewModel
import com.example.russianpath.presentation.screens.lesson.LessonResult
import com.example.russianpath.presentation.screens.lesson.LessonScreen
import com.example.russianpath.presentation.screens.lesson.RuleListScreen
import com.example.russianpath.presentation.screens.lesson.TopicLessonScreen
import com.example.russianpath.presentation.screens.profile.ProfileScreen
import com.example.russianpath.presentation.screens.result.ResultScreen
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {

    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")

    object Topic : Screen("topic/{topicId}") {
        fun createRoute(topicId: String) = "topic/$topicId"
    }

    object Rules : Screen("rules/{topicId}") {
        fun createRoute(topicId: String) = "rules/$topicId"
    }

    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }

    object Result : Screen("result/{resultJson}") {

        fun createRoute(result: LessonResult): String {
            val json = Gson().toJson(result)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "result/$encoded"
        }

        fun parseResult(arguments: Bundle?): LessonResult? {
            val encoded = arguments?.getString("resultJson")
            if (!encoded.isNullOrBlank()) {
                return try {
                    val json = URLDecoder.decode(encoded, "UTF-8")
                    Gson().fromJson(json, LessonResult::class.java)
                } catch (e: Exception) {
                    parseLegacyArgs(arguments)
                }
            }
            return parseLegacyArgs(arguments)
        }

        private fun parseLegacyArgs(arguments: Bundle?): LessonResult? {
            val lessonId = arguments?.getString("lessonId") ?: return null
            val stars = arguments.getInt("stars", 0)
            val xp = arguments.getInt("xp", 0)
            return LessonResult(
                lessonTitle = "",
                stars = stars,
                xpEarned = xp,
                scorePercent = 0,
                correctAnswers = 0,
                totalQuestions = 0,
                timeSpentSeconds = 0
            )
        }
    }
}

@Composable
fun NavGraph(
    onAppOpened: () -> Unit = {}
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            
            // Вызываем onAppOpened каждый раз при отображении Dashboard
            LaunchedEffect(Unit) {
                dashboardViewModel.onAppOpened()
            }
            
            DashboardScreen(
                onTopicClick = { topicId ->
                    navController.navigate(Screen.Topic.createRoute(topicId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                viewModel = dashboardViewModel
            )
        }

        composable(
            route = Screen.Topic.route,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
            TopicLessonScreen(
                topicId = topicId,
                onBackClick = { navController.popBackStack() },
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(lessonId))
                },
                onRulesClick = { rulesTopicId ->
                    navController.navigate(Screen.Rules.createRoute(rulesTopicId))
                }
            )
        }

        composable(
            route = Screen.Rules.route,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
            RuleListScreen(
                topicId = topicId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Lesson.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            LessonScreen(
                lessonId = lessonId,
                onBackClick = { navController.popBackStack() },
                onComplete = { result ->
                    navController.navigate(Screen.Result.createRoute(result)) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(navArgument("resultJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val result = Screen.Result.parseResult(backStackEntry.arguments)
            if (result != null) {
                ResultScreen(
                    result = result,
                    onContinue = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    },
                    onRepeat = {
                        navController.popBackStack(route = Screen.Dashboard.route, inclusive = false)
                    }
                )
            } else {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        composable(Screen.Profile.route) {
            ProfileScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
