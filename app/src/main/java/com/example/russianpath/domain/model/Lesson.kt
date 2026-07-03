package com.example.russianpath.domain.model

/**
 * Доменная модель урока.
 *
 * Представляет один урок внутри темы. Содержит теорию и набор вопросов.
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

    /**
     * Вычисляет награду XP в зависимости от результата.
     * При 100% правильных ответов добавляется бонус.
     */
    fun calculateXpReward(scorePercent: Int): Int {
        return if (scorePercent >= 100) {
            xpBaseReward + xpPerfectBonus
        } else {
            (xpBaseReward * scorePercent / 100).coerceAtLeast(1)
        }
    }

    /**
     * Определяет, пройден ли урок на основе процента правильных ответов.
     */
    fun isPassed(scorePercent: Int): Boolean {
        return scorePercent >= passingScorePercent
    }

    /**
     * Вычисляет количество звёзд на основе процента правильных ответов.
     */
    fun calculateStars(scorePercent: Int): Int {
        return when {
            scorePercent >= 95 -> maxStars
            scorePercent >= passingScorePercent -> ((maxStars * (scorePercent - passingScorePercent).toFloat()
                / (100 - passingScorePercent) + 1).toInt()).coerceIn(1, maxStars)
            else -> 0
        }
    }
}

/**
 * Тип урока.
 */
enum class LessonType {
    /** Обычный урок с упражнениями. */
    PRACTICE,

    /** Урок с теоретическим материалом. */
    THEORY,

    /** Тест для проверки знаний. */
    TEST,

    /** Диагностический тест (входной/промежуточный/итоговый). */
    DIAGNOSTIC,

    /** Бонусный урок (повышенная награда). */
    BONUS;

    companion object {
        fun fromString(value: String): LessonType {
            return try {
                valueOf(value.uppercase())
            } catch (_: IllegalArgumentException) {
                PRACTICE
            }
        }
    }
}
