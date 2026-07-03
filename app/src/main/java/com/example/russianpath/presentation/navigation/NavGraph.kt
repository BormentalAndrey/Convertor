package com.example.russianpath.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.russianpath.presentation.screens.dashboard.DashboardScreen
import com.example.russianpath.presentation.screens.lesson.LessonResult
import com.example.russianpath.presentation.screens.lesson.LessonScreen
import com.example.russianpath.presentation.screens.profile.ProfileScreen
import com.example.russianpath.presentation.screens.result.ResultScreen
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Маршруты навигации приложения.
 *
 * Каждый маршрут — sealed class с параметризованным путём.
 * Для сложных объектов используется JSON-сериализация в строку.
 */
sealed class Screen(val route: String) {

    /** Главный экран — список тем. */
    object Dashboard : Screen("dashboard")

    /** Экран профиля пользователя. */
    object Profile : Screen("profile")

    /** Экран урока. Параметр: lessonId. */
    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }

    /**
     * Экран результата после прохождения урока.
     * Параметры: lessonId, stars, xpEarned, scorePercent,
     * correctAnswers, totalQuestions, timeSpentSeconds, lessonTitle.
     *
     * Для избежания проблем с кодированием специальных символов
     * (запятые, пробелы, кириллица) используется URL-encoding.
     */
    object Result : Screen(
        "result/{resultJson}"
    ) {
        /**
         * Создаёт маршрут с упакованным LessonResult в JSON.
         * Для больших объектов безопаснее передавать через JSON,
         * чем через множество navArgument.
         */
        fun createRoute(result: LessonResult): String {
            val json = Gson().toJson(result)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "result/$encoded"
        }

        /**
         * Извлекает LessonResult из аргументов навигации.
         */
        fun parseResult(arguments: Bundle?): LessonResult? {
            val encoded = arguments?.getString("resultJson") ?: return null
            return try {
                val json = URLDecoder.decode(encoded, "UTF-8")
                Gson().fromJson(json, LessonResult::class.java)
            } catch (e: Exception) {
                // Fallback: пытаемся прочитать старые аргументы (обратная совместимость)
                parseLegacyArgs(arguments)
            }
        }

        /**
         * Парсинг старых аргументов для обратной совместимости.
         */
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

/**
 * Главный граф навигации приложения.
 *
 * Управляет переходами между экранами:
 * - Dashboard → Lesson → Result → Dashboard
 * - Dashboard → Profile → Dashboard
 *
 * @param onAppOpened Колбэк, вызываемый при старте приложения (для обновления стрика).
 */
@Composable
fun NavGraph(
    onAppOpened: () -> Unit = {}
) {
    val navController = rememberNavController()

    // Сигнализируем об открытии приложения
    remember {
        onAppOpened()
        true
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        // ====================================================================
        // Главный экран — Dashboard
        // ====================================================================
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

        // ====================================================================
        // Экран урока — Lesson
        // ====================================================================
        composable(
            route = Screen.Lesson.route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable

            LessonScreen(
                lessonId = lessonId,
                onBackClick = {
                    navController.popBackStack()
                },
                onComplete = { result ->
                    navController.navigate(
                        Screen.Result.createRoute(result)
                    ) {
                        // Убираем Lesson из back stack, чтобы кнопка "Назад"
                        // с экрана результата возвращала на Dashboard
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }

        // ====================================================================
        // Экран результата — Result
        // ====================================================================
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("resultJson") { type = NavType.StringType }
            )
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
                        // Возвращаемся к уроку (он уже убран из стека,
                        // но lessonId есть в result)
                        navController.popBackStack(
                            route = Screen.Dashboard.route,
                            inclusive = false
                        )
                    }
                )
            } else {
                // Если результат не распарсился — возвращаемся на Dashboard
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        // ====================================================================
        // Экран профиля — Profile
        // ====================================================================
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
