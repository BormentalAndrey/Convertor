package com.example.russianpath.domain.model

/**
 * Доменная модель статистики пользователя.
 *
 * Агрегирует все показатели прогресса для отображения в ProfileScreen,
 * DashboardScreen и виджетах.
 */
data class UserStats(
    /** Общее количество опыта. */
    val totalXp: Int = 0,

    /** Текущий уровень пользователя. */
    val level: Int = 1,

    /** Количество XP до следующего уровня. */
    val xpToNextLevel: Int = 100,

    /** Текущий стрик (дней подряд). */
    val currentStreak: Int = 0,

    /** Максимальный стрик за всё время. */
    val longestStreak: Int = 0,

    /** Баланс самоцветов. */
    val gemsBalance: Int = 50,

    /** Текущее количество жизней. */
    val livesCount: Int = 5,

    /** Максимальное количество жизней. */
    val maxLives: Int = 5,

    /** Общее количество завершённых уроков. */
    val totalLessonsCompleted: Int = 0,

    /** Количество уроков, пройденных идеально (100%, 3 звезды). */
    val totalPerfectLessons: Int = 0,

    /** Общее количество допущенных ошибок. */
    val totalMistakesCount: Int = 0,

    /** Общее время в приложении (в секундах). */
    val totalTimeSpentSeconds: Long = 0L,

    /** Общее количество дней активности (не только стрик). */
    val totalDaysActive: Int = 0,

    /** ID текущего класса пользователя. */
    val currentGradeId: String = "",

    /** ID текущей темы пользователя. */
    val currentTopicId: String = "",

    /** Пройден ли онбординг. */
    val onboardingCompleted: Boolean = false,

    /** Точность ответов в процентах (0–100). */
    val accuracy: Float = 100f
) {

    /**
     * Вычисляет прогресс до следующего уровня в процентах (0–100).
     */
    fun getLevelProgressPercent(): Float {
        val totalForLevel = xpForLevel(level + 1) - xpForLevel(level)
        if (totalForLevel <= 0) return 100f
        val progressInLevel = totalXp - xpForLevel(level)
        return (progressInLevel.toFloat() / totalForLevel * 100).coerceIn(0f, 100f)
    }

    /**
     * Возвращает XP, необходимое для достижения указанного уровня.
     */
    private fun xpForLevel(lvl: Int): Int {
        val l = lvl - 1
        return l * l * 50
    }

    /**
     * Форматирует общее время в human-readable строку.
     */
    fun getFormattedTotalTime(): String {
        val hours = totalTimeSpentSeconds / 3600
        val minutes = (totalTimeSpentSeconds % 3600) / 60
        return when {
            hours > 0 -> "${hours} ч ${minutes} мин"
            minutes > 0 -> "${minutes} мин"
            else -> "< 1 мин"
        }
    }

    /**
     * Возвращает текстовое описание уровня.
     */
    fun getLevelTitle(): String {
        return when {
            level < 5 -> "Новичок"
            level < 10 -> "Ученик"
            level < 20 -> "Знаток"
            level < 35 -> "Эксперт"
            level < 50 -> "Мастер"
            else -> "Грандмастер"
        }
    }

    /**
     * Вычисляет процент заполнения шкалы жизней.
     */
    fun getLivesPercent(): Float {
        if (maxLives == 0) return 0f
        return (livesCount.toFloat() / maxLives * 100).coerceIn(0f, 100f)
    }

    companion object {
        /** Создаёт UserStats с значениями по умолчанию для нового пользователя. */
        fun createDefault(): UserStats = UserStats()
    }
}
