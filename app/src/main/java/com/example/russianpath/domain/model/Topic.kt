package com.example.russianpath.domain.model

/**
 * Доменная модель темы обучения.
 *
 * Представляет тему внутри раздела (например, "Правописание приставок ПРЕ-/ПРИ-").
 * Используется в Presentation-слое для отображения списка тем.
 */
data class Topic(
    /** Уникальный идентификатор темы. */
    val id: String,

    /** ID раздела, к которому относится тема. */
    val sectionId: String = "",

    /** ID класса (например, "5", "oge"). */
    val gradeId: String = "",

    /** Название темы для отображения. */
    val title: String,

    /** Описание темы. */
    val description: String,

    /** Имя иконки для UI. */
    val iconName: String,

    /** Порядок сортировки внутри раздела/класса. */
    val sortOrder: Int,

    /** Разблокирована ли тема для пользователя. */
    val isUnlocked: Boolean = false,

    /** Уровень сложности (1–5). */
    val difficultyLevel: Int = 1,

    /** Примерное время прохождения в минутах. */
    val estimatedMinutes: Int = 15,

    /** Список ID тем, которые нужно пройти перед этой. */
    val prerequisiteTopicIds: List<String> = emptyList(),

    /** Процент завершения (0–100). Вычисляется на основе пройденных уроков. */
    val completionPercentage: Float = 0f,

    /** Общее количество звёзд, полученных за уроки темы. */
    val stars: Int = 0,

    /** Количество уроков в теме. */
    val totalLessons: Int = 0,

    /** Количество пройденных уроков. */
    val completedLessons: Int = 0
) {

    /**
     * Проверяет, все ли пререквизиты выполнены.
     * Используется для определения доступности темы.
     */
    fun arePrerequisitesMet(completedTopicIds: Set<String>): Boolean {
        return prerequisiteTopicIds.all { it in completedTopicIds }
    }

    /**
     * Вычисляет процент завершения на основе количества уроков.
     */
    fun calculateCompletionPercentage(): Float {
        if (totalLessons == 0) return 0f
        return (completedLessons.toFloat() / totalLessons * 100).coerceIn(0f, 100f)
    }

    /**
     * Возвращает текстовое описание сложности.
     */
    fun getDifficultyLabel(): String {
        return when (difficultyLevel) {
            1 -> "Начальный"
            2 -> "Базовый"
            3 -> "Средний"
            4 -> "Продвинутый"
            5 -> "Экспертный"
            else -> "Неизвестно"
        }
    }
}
