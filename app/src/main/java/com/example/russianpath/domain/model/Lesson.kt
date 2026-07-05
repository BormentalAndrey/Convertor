// app/src/main/java/com/example/russianpath/domain/model/Lesson.kt

package com.example.russianpath.domain.model

/**
 * Доменная модель урока.
 *
 * Представляет один урок внутри темы. Содержит теорию, текст упражнения и набор вопросов.
 */
data class Lesson(
    /** Уникальный идентификатор урока. */
    val id: String,

    /** ID темы, к которой относится урок. */
    val topicId: String,

    /** ID цели обучения, с которой связан урок. */
    val primaryObjectiveId: String = "",

    /** Тип урока (practice, theory, test, diagnostic, bonus). */
    val lessonType: LessonType = LessonType.PRACTICE,

    /** Название урока. */
    val title: String,

    /** Описание урока. */
    val description: String = "",

    /** Текст инструкции к уроку. */
    val instructionText: String = "",

    /** Текст упражнения для чтения в формате JSON. */
    val exerciseTextJson: String = "{}",

    /** Уровень сложности (1–5). */
    val difficulty: Int = 1,

    /** Порядок сортировки внутри темы. */
    val sortOrder: Int = 0,

    /** Теоретический материал в формате JSON. */
    val theoryJson: String = "{}",

    /** Количество вопросов в уроке. */
    val questionsCount: Int = 0,

    /** Ограничение по времени в секундах (0 — без ограничения). */
    val timeLimitSeconds: Int = 0,

    /** Порог прохождения в процентах. */
    val passingScorePercent: Int = 70,

    /** Максимальное количество звёзд. */
    val maxStars: Int = 3,

    /** Базовая награда XP. */
    val xpBaseReward: Int = 50,

    /** Бонус XP за идеальное прохождение. */
    val xpPerfectBonus: Int = 25,

    /** Награда в самоцветах. */
    val gemsReward: Int = 5,

    /** Является ли бонусным уроком. */
    val isBonus: Boolean = false,

    /** Является ли диагностическим тестом. */
    val isDiagnostic: Boolean = false,

    /** Пройден ли урок пользователем. */
    val isCompleted: Boolean = false,

    /** Лучший результат в звёздах. */
    val bestStars: Int = 0,

    /** Лучший процент правильных ответов. */
    val bestScorePercent: Int = 0,

    /** Количество попыток прохождения. */
    val attemptCount: Int = 0
) {

    fun calculateXpReward(scorePercent: Int): Int {
        return if (scorePercent >= 100) xpBaseReward + xpPerfectBonus
        else (xpBaseReward * scorePercent / 100).coerceAtLeast(1)
    }

    fun isPassed(scorePercent: Int): Boolean {
        return scorePercent >= passingScorePercent
    }

    fun calculateStars(scorePercent: Int): Int {
        return when {
            scorePercent >= 95 -> maxStars
            scorePercent >= passingScorePercent -> ((maxStars * (scorePercent - passingScorePercent).toFloat()
                / (100 - passingScorePercent) + 1).toInt()).coerceIn(1, maxStars)
            else -> 0
        }
    }

    /** Извлекает текст упражнения из JSON */
    fun getExerciseText(): String {
        if (exerciseTextJson == "{}" || exerciseTextJson.isBlank()) return ""
        return try {
            com.google.gson.Gson().fromJson(exerciseTextJson, Map::class.java)?.get("text") as? String ?: ""
        } catch (_: Exception) { "" }
    }

    /** Извлекает текст теории из JSON */
    fun getTheoryText(): String {
        if (theoryJson == "{}" || theoryJson.isBlank()) return ""
        return try {
            com.google.gson.Gson().fromJson(theoryJson, Map::class.java)?.get("text") as? String ?: ""
        } catch (_: Exception) { "" }
    }
}

enum class LessonType {
    PRACTICE, THEORY, TEST, DIAGNOSTIC, BONUS;

    companion object {
        fun fromString(value: String): LessonType {
            return try { valueOf(value.uppercase()) }
            catch (_: IllegalArgumentException) { PRACTICE }
        }
    }
}
