// app/src/main/java/com/example/russianpath/data/local/dao/UserProgressDao.kt

package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    /**
     * Возвращает прогресс пользователя (синглтон-запись с id = 1).
     * Flow для реактивного обновления UI.
     */
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun observeUserProgress(): Flow<UserProgressEntity?>

    /**
     * Возвращает прогресс пользователя однократно.
     */
    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getUserProgress(): UserProgressEntity?

    /**
     * Вставка или обновление всего прогресса.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: UserProgressEntity)

    /**
     * Добавление XP и обновление уровня.
     */
    @Query(
        """
        UPDATE user_progress
        SET total_xp = total_xp + :xp,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun addXp(xp: Int, now: Long = System.currentTimeMillis())

    /**
     * Обновление информации об уровне.
     * Вызывается после проверки достижения нового уровня.
     */
    @Query(
        """
        UPDATE user_progress
        SET current_level = :level,
            xp_to_next_level = :xpToNext,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun updateLevel(level: Int, xpToNext: Int, now: Long = System.currentTimeMillis())

    /**
     * Добавление самоцветов.
     */
    @Query(
        """
        UPDATE user_progress
        SET gems_balance = gems_balance + :gems,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun addGems(gems: Int, now: Long = System.currentTimeMillis())

    /**
     * Списание самоцветов.
     * Возвращает количество изменённых строк (0 — недостаточно самоцветов).
     */
    @Query(
        """
        UPDATE user_progress
        SET gems_balance = gems_balance - :gems,
            updated_at = :now
        WHERE id = 1 AND gems_balance >= :gems
        """
    )
    suspend fun spendGems(gems: Int, now: Long = System.currentTimeMillis()): Int

    /**
     * Обновление количества жизней.
     */
    @Query(
        """
        UPDATE user_progress
        SET lives_count = :lives,
            last_life_refill_time = :now,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun updateLives(lives: Int, now: Long = System.currentTimeMillis())

    /**
     * Обновление жизней и времени последнего восстановления.
     */
    @Query(
        """
        UPDATE user_progress
        SET lives_count = :lives,
            last_life_refill_time = :refillTime,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun updateLivesAndRefillTime(lives: Int, refillTime: Long, now: Long = System.currentTimeMillis())

    /**
     * Обновление только времени последнего восстановления жизней.
     */
    @Query(
        """
        UPDATE user_progress
        SET last_life_refill_time = :refillTime,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun updateLastLifeRefillTime(refillTime: Long, now: Long = System.currentTimeMillis())

    /**
     * Потеря одной жизни (с проверкой, что жизни > 0).
     */
    @Query(
        """
        UPDATE user_progress
        SET lives_count = MAX(0, lives_count - 1),
            updated_at = :now
        WHERE id = 1 AND lives_count > 0
        """
    )
    suspend fun loseLife(now: Long = System.currentTimeMillis())

    /**
     * Потеря жизни со сбросом таймера восстановления.
     * Используется когда жизни были на максимуме и начинают тратиться.
     */
    @Query(
        """
        UPDATE user_progress
        SET lives_count = MAX(0, lives_count - 1),
            last_life_refill_time = :refillTime,
            updated_at = :now
        WHERE id = 1 AND lives_count > 0
        """
    )
    suspend fun loseLifeAndResetRefillTime(refillTime: Long, now: Long = System.currentTimeMillis())

    /**
     * Восстановление одной жизни (с проверкой максимума).
     */
    @Query(
        """
        UPDATE user_progress
        SET lives_count = MIN(max_lives, lives_count + 1),
            last_life_refill_time = :now,
            updated_at = :now
        WHERE id = 1 AND lives_count < max_lives
        """
    )
    suspend fun refillLife(now: Long = System.currentTimeMillis())

    /**
     * Обновление стрика.
     */
    @Query(
        """
        UPDATE user_progress
        SET current_streak = :streak,
            longest_streak = MAX(longest_streak, :streak),
            streak_start_date = :streakStartDate,
            last_active_date = :lastActiveDate,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun updateStreak(
        streak: Int,
        streakStartDate: Long,
        lastActiveDate: Long,
        now: Long = System.currentTimeMillis()
    )

    /**
     * Сброс стрика.
     */
    @Query(
        """
        UPDATE user_progress
        SET current_streak = 0,
            streak_start_date = 0,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun resetStreak(now: Long = System.currentTimeMillis())

    /**
     * Увеличение счётчика завершённых уроков.
     */
    @Query(
        """
        UPDATE user_progress
        SET total_lessons_completed = total_lessons_completed + 1,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun incrementLessonsCompleted(now: Long = System.currentTimeMillis())

    /**
     * Увеличение счётчика идеальных уроков.
     */
    @Query(
        """
        UPDATE user_progress
        SET total_perfect_lessons = total_perfect_lessons + 1,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun incrementPerfectLessons(now: Long = System.currentTimeMillis())

    /**
     * Добавление общего времени.
     */
    @Query(
        """
        UPDATE user_progress
        SET total_time_spent_seconds = total_time_spent_seconds + :seconds,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun addTimeSpent(seconds: Long, now: Long = System.currentTimeMillis())

    /**
     * Увеличение счётчика ошибок.
     */
    @Query(
        """
        UPDATE user_progress
        SET total_mistakes_count = total_mistakes_count + :count,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun addMistakes(count: Int, now: Long = System.currentTimeMillis())

    /**
     * Обновление текущего класса и темы.
     */
    @Query(
        """
        UPDATE user_progress
        SET current_grade_id = :gradeId,
            current_topic_id = :topicId,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun updateCurrentPosition(
        gradeId: String,
        topicId: String,
        now: Long = System.currentTimeMillis()
    )

    /**
     * Завершение онбординга.
     */
    @Query(
        """
        UPDATE user_progress
        SET onboarding_completed = 1,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun completeOnboarding(now: Long = System.currentTimeMillis())

    /**
     * Обновление времени синхронизации.
     */
    @Query(
        """
        UPDATE user_progress
        SET last_sync_time = :syncTime,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun updateSyncTime(syncTime: Long, now: Long = System.currentTimeMillis())

    /**
     * Увеличение счётчика активных дней.
     */
    @Query(
        """
        UPDATE user_progress
        SET total_days_active = total_days_active + 1,
            updated_at = :now
        WHERE id = 1
        """
    )
    suspend fun incrementDaysActive(now: Long = System.currentTimeMillis())
}
