package com.example.russianpath.data.repository

import com.example.russianpath.data.local.dao.LessonCompletionDao
import com.example.russianpath.data.local.dao.UserProgressDao
import com.example.russianpath.data.local.entity.LessonCompletionEntity
import com.example.russianpath.data.local.entity.UserProgressEntity
import com.example.russianpath.domain.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для управления прогрессом пользователя.
 *
 * Объединяет операции над UserProgressEntity (общий прогресс)
 * и LessonCompletionEntity (история прохождений уроков).
 *
 * Все методы потокобезопасны (Room + Coroutines).
 */
@Singleton
class UserRepository @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val lessonCompletionDao: LessonCompletionDao
) {

    // ========================================================================
    // Статистика пользователя
    // ========================================================================

    /**
     * Возвращает Flow со статистикой пользователя для реактивного обновления UI.
     * Если записи прогресса ещё нет (первый запуск), возвращает UserStats по умолчанию.
     */
    fun observeUserStats(): Flow<UserStats> {
        return userProgressDao.observeUserProgress().map { entity ->
            if (entity != null) {
                entity.toDomainModel()
            } else {
                UserStats()
            }
        }
    }

    /**
     * Возвращает текущую статистику однократно (для не-UI операций).
     */
    suspend fun getUserStats(): UserStats {
        val entity = userProgressDao.getUserProgress()
        return entity?.toDomainModel() ?: UserStats()
    }

    // ========================================================================
    // Управление опытом (XP) и уровнем
    // ========================================================================

    /**
     * Добавляет опыт пользователю.
     * Если накопленного XP достаточно для нового уровня — повышает уровень.
     *
     * @param amount Количество XP для добавления.
     */
    suspend fun addXp(amount: Int) {
        val now = System.currentTimeMillis()
        userProgressDao.addXp(amount, now)

        // Проверяем, не пора ли повысить уровень
        val progress = userProgressDao.getUserProgress() ?: return
        val totalXp = progress.totalXp + amount
        val newLevel = calculateLevel(totalXp)
        if (newLevel > progress.currentLevel) {
            val xpToNext = calculateXpForLevel(newLevel + 1) - totalXp
            userProgressDao.updateLevel(newLevel, maxOf(0, xpToNext), now)
        }
    }

    /**
     * Формула расчёта уровня на основе общего XP.
     * Использует квадратичную прогрессию: level = floor(sqrt(totalXp / 50)) + 1
     * Это даёт замедляющийся рост уровней.
     */
    private fun calculateLevel(totalXp: Int): Int {
        return kotlin.math.sqrt((totalXp / 50.0)).toInt() + 1
    }

    /**
     * Вычисляет XP, необходимое для достижения указанного уровня.
     */
    private fun calculateXpForLevel(level: Int): Int {
        val lvl = level - 1
        return lvl * lvl * 50
    }

    // ========================================================================
    // Управление самоцветами
    // ========================================================================

    /**
     * Добавляет самоцветы.
     */
    suspend fun addGems(amount: Int) {
        val now = System.currentTimeMillis()
        userProgressDao.addGems(amount, now)
    }

    /**
     * Тратит самоцветы.
     *
     * @return true если операция успешна (достаточно самоцветов), false иначе.
     */
    suspend fun spendGems(amount: Int): Boolean {
        val now = System.currentTimeMillis()
        val updatedRows = userProgressDao.spendGems(amount, now)
        return updatedRows > 0
    }

    /**
     * Возвращает текущий баланс самоцветов.
     */
    suspend fun getGemsBalance(): Int {
        return userProgressDao.getUserProgress()?.gemsBalance ?: 0
    }

    // ========================================================================
    // Управление жизнями
    // ========================================================================

    /**
     * Теряет одну жизнь.
     * Жизни не могут упасть ниже 0.
     */
    suspend fun loseLife() {
        val now = System.currentTimeMillis()
        userProgressDao.loseLife(now)
    }

    /**
     * Восстанавливает одну жизнь.
     * Жизни не могут превысить maxLives.
     */
    suspend fun refillLife() {
        val now = System.currentTimeMillis()
        userProgressDao.refillLife(now)
    }

    /**
     * Устанавливает количество жизней в указанное значение.
     * Используется для полного восстановления (например, покупка жизней).
     */
    suspend fun setLives(count: Int) {
        val now = System.currentTimeMillis()
        val progress = userProgressDao.getUserProgress() ?: return
        val clamped = count.coerceIn(0, progress.maxLives)
        userProgressDao.updateLives(clamped, now)
    }

    /**
     * Возвращает текущее количество жизней.
     */
    suspend fun getLivesCount(): Int {
        return userProgressDao.getUserProgress()?.livesCount ?: 0
    }

    // ========================================================================
    // Управление стриком
    // ========================================================================

    /**
     * Обновляет стрик на основе текущей даты.
     * Если последняя активность была вчера — увеличивает стрик.
     * Если позже — сбрасывает стрик и начинает новый.
     * Если сегодня уже была активность — не меняет.
     */
    suspend fun updateStreak() {
        val now = System.currentTimeMillis()
        val progress = userProgressDao.getUserProgress() ?: return

        val today = now / 86_400_000 // дни от эпохи
        val lastActive = progress.lastActiveDate / 86_400_000

        when {
            lastActive == today -> {
                // Уже обновляли сегодня — ничего не делаем
                return
            }
            lastActive == today - 1 -> {
                // Активность вчера — продолжаем стрик
                val newStreak = progress.currentStreak + 1
                val streakStart = if (progress.currentStreak == 0) now else progress.streakStartDate
                userProgressDao.updateStreak(newStreak, streakStart, now, now)
            }
            else -> {
                // Разрыв больше одного дня — начинаем новый стрик
                userProgressDao.updateStreak(1, now, now, now)
            }
        }
    }

    // ========================================================================
    // Завершение урока
    // ========================================================================

    /**
     * Сохраняет результат завершения урока.
     *
     * Обновляет:
     * - Историю прохождений (LessonCompletionEntity)
     * - Общий прогресс (XP, счётчики уроков, ошибок, времени)
     * - Стрик
     *
     * @param lessonId ID урока.
     * @param topicId ID темы (для аналитики).
     * @param stars Количество заработанных звёзд.
     * @param scorePercent Процент правильных ответов.
     * @param correctAnswers Количество правильных ответов.
     * @param totalQuestions Общее количество вопросов.
     * @param mistakesCount Количество ошибок.
     * @param mistakesJson JSON с деталями ошибок для аналитики.
     * @param timeSpentSeconds Время прохождения в секундах.
     * @param xpEarned Заработанный опыт.
     * @param gemsEarned Заработанные самоцветы.
     * @param isPassed Пройден ли урок (достигнут порог).
     */
    suspend fun completeLesson(
        lessonId: String,
        topicId: String = "",
        stars: Int,
        scorePercent: Int,
        correctAnswers: Int,
        totalQuestions: Int,
        mistakesCount: Int,
        mistakesJson: String = "[]",
        timeSpentSeconds: Int,
        xpEarned: Int,
        gemsEarned: Int = 0,
        isPassed: Boolean = true
    ) {
        val now = System.currentTimeMillis()

        // Получаем номер попытки
        val attemptNumber = lessonCompletionDao.getAttemptCount(lessonId) + 1

        // Сохраняем результат в историю
        val completion = LessonCompletionEntity(
            id = UUID.randomUUID().toString(),
            lessonId = lessonId,
            topicId = topicId,
            stars = stars,
            scorePercent = scorePercent,
            correctAnswers = correctAnswers,
            totalQuestions = totalQuestions,
            mistakesCount = mistakesCount,
            mistakesJson = mistakesJson,
            timeSpentSeconds = timeSpentSeconds,
            completedAt = now,
            xpEarned = xpEarned,
            gemsEarned = gemsEarned,
            attemptNumber = attemptNumber,
            isPassed = isPassed,
            deviceId = "",
            schemaVersion = 1,
            syncedAt = 0
        )
        lessonCompletionDao.saveCompletion(completion)

        // Обновляем общий прогресс
        userProgressDao.incrementLessonsCompleted(now)
        if (stars == 3 && scorePercent == 100) {
            userProgressDao.incrementPerfectLessons(now)
        }
        userProgressDao.addMistakes(mistakesCount, now)
        userProgressDao.addTimeSpent(timeSpentSeconds.toLong(), now)

        // Добавляем XP и самоцветы
        addXp(xpEarned)
        if (gemsEarned > 0) {
            addGems(gemsEarned)
        }

        // Обновляем стрик
        updateStreak()
    }

    // ========================================================================
    // Дополнительные методы
    // ========================================================================

    /**
     * Обновляет текущую позицию пользователя (класс и тему).
     * Используется для быстрого возврата к месту остановки.
     */
    suspend fun updateCurrentPosition(gradeId: String, topicId: String) {
        val now = System.currentTimeMillis()
        userProgressDao.updateCurrentPosition(gradeId, topicId, now)
    }

    /**
     * Отмечает онбординг как завершённый.
     */
    suspend fun completeOnboarding() {
        val now = System.currentTimeMillis()
        userProgressDao.completeOnboarding(now)
    }

    /**
     * Проверяет, пройден ли онбординг.
     */
    suspend fun isOnboardingCompleted(): Boolean {
        return userProgressDao.getUserProgress()?.onboardingCompleted ?: false
    }

    /**
     * Возвращает ID текущего класса пользователя.
     */
    suspend fun getCurrentGradeId(): String {
        return userProgressDao.getUserProgress()?.currentGradeId ?: ""
    }

    /**
     * Возвращает ID текущей темы пользователя.
     */
    suspend fun getCurrentTopicId(): String {
        return userProgressDao.getUserProgress()?.currentTopicId ?: ""
    }

    // ========================================================================
    // Маппинг в доменную модель
    // ========================================================================

    /**
     * Преобразует UserProgressEntity в доменную модель UserStats.
     */
    private fun UserProgressEntity.toDomainModel(): UserStats {
        return UserStats(
            totalXp = totalXp,
            level = currentLevel,
            xpToNextLevel = xpToNextLevel,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            gemsBalance = gemsBalance,
            livesCount = livesCount,
            maxLives = maxLives,
            totalLessonsCompleted = totalLessonsCompleted,
            totalPerfectLessons = totalPerfectLessons,
            totalMistakesCount = totalMistakesCount,
            totalTimeSpentSeconds = totalTimeSpentSeconds,
            totalDaysActive = totalDaysActive,
            currentGradeId = currentGradeId,
            currentTopicId = currentTopicId,
            onboardingCompleted = onboardingCompleted,
            accuracy = calculateAccuracy()
        )
    }

    /**
     * Вычисляет точность ответов в процентах.
     */
    private fun UserProgressEntity.calculateAccuracy(): Float {
        val totalAnswers = totalLessonsCompleted * 10 // примерная оценка
        if (totalAnswers == 0) return 100f
        val correctAnswers = totalAnswers - totalMistakesCount
        return (correctAnswers.toFloat() / totalAnswers * 100).coerceIn(0f, 100f)
    }
}
