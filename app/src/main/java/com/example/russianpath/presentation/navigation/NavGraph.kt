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
 * Маршруты навигации приложения «Русский Путь».
 *
 * Каждый маршрут — sealed class с параметризованным путём.
 * Для передачи сложных объектов (LessonResult) между экранами
 * используется JSON-сериализация с URL-encoding для безопасной
 * передачи кириллицы и специальных символов.
 *
 * Граф навигации:
 * ```
 * Dashboard ──→ Lesson ──→ Result ──→ Dashboard
 *     │                                    ↑
 *     └──→ Profile ────────────────────────┘
 * ```
 */
sealed class Screen(val route: String) {

    /** Главный экран — список тем. */
    object Dashboard : Screen("dashboard")

    /** Экран профиля пользователя. */
    object Profile : Screen("profile")

    /** Экран урока. Параметр: lessonId (String). */
    object Lesson : Screen("lesson/{lessonId}") {
        /** Создаёт маршрут с указанным ID урока. */
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }

    /**
     * Экран результата после прохождения урока.
     *
     * Параметр: resultJson — URL-encoded JSON с объектом LessonResult.
     *
     * Использование JSON вместо множества navArgument решает проблемы:
     * - Ограничение длины URL (7 параметров × длинные значения)
     * - Кириллица в lessonTitle (небезопасна в URL без кодирования)
     * - Расширяемость (добавление полей в LessonResult не ломает навигацию)
     */
    object Result : Screen("result/{resultJson}") {

        /**
         * Создаёт маршрут с упакованным LessonResult.
         *
         * @param result Результат прохождения урока.
         * @return Строка маршрута вида "result/{url-encoded-json}".
         */
        fun createRoute(result: LessonResult): String {
            val json = Gson().toJson(result)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "result/$encoded"
        }

        /**
         * Извлекает LessonResult из аргументов навигации.
         *
         * Поддерживает два формата:
         * 1. Новый: resultJson = URL-encoded JSON
         * 2. Старый (legacy): lessonId, stars, xp как отдельные аргументы
         *
         * @param arguments Bundle с аргументами навигации.
         * @return LessonResult или null, если не удалось распарсить.
         */
        fun parseResult(arguments: Bundle?): LessonResult? {
            // Пробуем новый формат (JSON)
            val encoded = arguments?.getString("resultJson")
            if (!encoded.isNullOrBlank()) {
                return try {
                    val json = URLDecoder.decode(encoded, "UTF-8")
                    Gson().fromJson(json, LessonResult::class.java)
                } catch (e: Exception) {
                    // JSON повреждён — пробуем legacy
                    parseLegacyArgs(arguments)
                }
            }
            // Пробуем старый формат
            return parseLegacyArgs(arguments)
        }

        /**
         * Парсинг старых аргументов для обратной совместимости.
         *
         * Используется, если пользователь обновил приложение,
         * находясь на экране результата (редкий, но возможный сценарий).
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
 * - Dashboard → Lesson (по нажатию на тему)
 * - Lesson → Result (при завершении урока)
 * - Result → Dashboard (по кнопке «Продолжить»)
 * - Dashboard → Profile (по кнопке профиля)
 * - Profile → Dashboard (по кнопке «Назад»)
 *
 * Back stack управляется так:
 * - При переходе Lesson → Result урок убирается из стека (popUpTo Dashboard)
 * - При переходе Result → Dashboard стек очищается полностью
 * - Кнопка «Повторить» на Result возвращает на Dashboard (урок нужно выбрать заново)
 *
 * @param onAppOpened Колбэк, вызываемый при первом создании NavGraph.
 *                     Используется для обновления стрика пользователя.
 */
@Composable
fun NavGraph(
    onAppOpened: () -> Unit = {}
) {
    val navController = rememberNavController()

    // Сигнализируем об открытии приложения при первом composable
    // remember с Unit гарантирует однократный вызов
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
                        // Убираем Lesson из back stack.
                        // Кнопка «Назад» с экрана результата будет возвращать на Dashboard,
                        // а не на пройденный урок.
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
                        // Переход на Dashboard с очисткой всего стека
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    },
                    onRepeat = {
                        // Возвращаемся на Dashboard.
                        // Урок уже убран из стека при переходе Lesson → Result,
                        // поэтому пользователю нужно снова выбрать урок.
                        navController.popBackStack(
                            route = Screen.Dashboard.route,
                            inclusive = false
                        )
                    }
                )
            } else {
                // Если результат не распарсился — аварийный возврат на Dashboard
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
