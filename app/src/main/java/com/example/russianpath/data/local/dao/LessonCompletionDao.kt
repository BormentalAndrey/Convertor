package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.LessonCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonCompletionDao {

    /**
     * Сохранение результата прохождения урока.
     * Каждая попытка создаёт новую запись с уникальным ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCompletion(completion: LessonCompletionEntity)

    /**
     * Массовое сохранение (для синхронизации).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCompletions(completions: List<LessonCompletionEntity>)

    /**
     * Получение последнего результата по ID урока.
     * Индекс: idx_completions_lesson_date
     */
    @Query(
        """
        SELECT * FROM lesson_completions
        WHERE lesson_id = :lessonId
        ORDER BY completed_at DESC
        LIMIT 1
        """
    )
    suspend fun getLatestCompletion(lessonId: String): LessonCompletionEntity?

    /**
     * Flow-версия последнего результата.
     */
    @Query(
        """
        SELECT * FROM lesson_completions
        WHERE lesson_id = :lessonId
        ORDER BY completed_at DESC
        LIMIT 1
        """
    )
    fun observeLatestCompletion(lessonId: String): Flow<LessonCompletionEntity?>

    /**
     * Получение всех попыток по ID урока.
     */
    @Query(
        """
        SELECT * FROM lesson_completions
        WHERE lesson_id = :lessonId
        ORDER BY completed_at DESC
        """
    )
    fun observeAllCompletions(lessonId: String): Flow<List<LessonCompletionEntity>>

    /**
     * Получение лучшего результата по ID урока.
     */
    @Query(
        """
        SELECT * FROM lesson_completions
        WHERE lesson_id = :lessonId AND is_passed = 1
        ORDER BY stars DESC, score_percent DESC
        LIMIT 1
        """
    )
    suspend fun getBestCompletion(lessonId: String): LessonCompletionEntity?

    /**
     * Получение количества попыток по ID урока.
     */
    @Query(
        """
        SELECT COUNT(*) FROM lesson_completions
        WHERE lesson_id = :lessonId
        """
    )
    suspend fun getAttemptCount(lessonId: String): Int

    /**
     * Получение завершённых уроков по ID темы.
     * Индекс: idx_completions_topic_date
     */
    @Query(
        """
        SELECT DISTINCT lesson_id FROM lesson_completions
        WHERE topic_id = :topicId AND is_passed = 1
        """
    )
    suspend fun getPassedLessonIds(topicId: String): List<String>

    /**
     * Получение количества пройденных уроков в теме.
     */
    @Query(
        """
        SELECT COUNT(DISTINCT lesson_id) FROM lesson_completions
        WHERE topic_id = :topicId AND is_passed = 1
        """
    )
    suspend fun getPassedLessonCount(topicId: String): Int

    /**
     * Получение всех завершений за период (для аналитики).
     */
    @Query(
        """
        SELECT * FROM lesson_completions
        WHERE completed_at BETWEEN :startTime AND :endTime
        ORDER BY completed_at DESC
        """
    )
    suspend fun getCompletionsInPeriod(
        startTime: Long,
        endTime: Long
    ): List<LessonCompletionEntity>

    /**
     * Получение общего количества звёзд.
     */
    @Query(
        """
        SELECT COALESCE(SUM(stars), 0) FROM lesson_completions
        """
    )
    suspend fun getTotalStars(): Int

    /**
     * Получение общего XP.
     */
    @Query(
        """
        SELECT COALESCE(SUM(xp_earned), 0) FROM lesson_completions
        """
    )
    suspend fun getTotalXpEarned(): Int

    /**
     * Получение последних активностей (для виджета «Недавние»).
     */
    @Query(
        """
        SELECT * FROM lesson_completions
        ORDER BY completed_at DESC
        LIMIT :limit
        """
    )
    fun observeRecentCompletions(limit: Int = 10): Flow<List<LessonCompletionEntity>>

    /**
     * Получение незасинхронизированных записей.
     */
    @Query(
        """
        SELECT * FROM lesson_completions
        WHERE synced_at = 0
        ORDER BY completed_at ASC
        LIMIT :limit
        """
    )
    suspend fun getUnsyncedCompletions(limit: Int = 100): List<LessonCompletionEntity>

    /**
     * Отметка записи как синхронизированной.
     */
    @Query(
        """
        UPDATE lesson_completions
        SET synced_at = :syncTime
        WHERE id = :id
        """
    )
    suspend fun markAsSynced(id: String, syncTime: Long = System.currentTimeMillis())

    /**
     * Удаление старых записей (очистка истории старше N дней).
     * Для управления размером БД.
     */
    @Query(
        """
        DELETE FROM lesson_completions
        WHERE completed_at < :beforeTime
        """
    )
    suspend fun deleteOlderThan(beforeTime: Long): Int
}
