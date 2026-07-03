package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    /**
     * Возвращает активные уроки по ID темы, отсортированные по порядку.
     * Основной запрос для списка уроков в теме.
     * Индекс: idx_lessons_topic_sort (topic_id, sort_order)
     */
    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByTopic(topicId: String): Flow<List<LessonEntity>>

    /**
     * Возвращает все уроки по ID темы, включая неактивные.
     */
    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId
        ORDER BY sort_order ASC
        """
    )
    suspend fun getAllByTopic(topicId: String): List<LessonEntity>

    /**
     * Возвращает уроки по типу внутри темы.
     * Индекс: idx_lessons_topic_type_sort (topic_id, lesson_type, sort_order)
     */
    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId
          AND lesson_type = :lessonType
          AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByTopicAndType(
        topicId: String,
        lessonType: String
    ): Flow<List<LessonEntity>>

    /**
     * Поиск урока по локальному ID.
     */
    @Query(
        """
        SELECT * FROM lessons
        WHERE id = :id
        """
    )
    suspend fun getById(id: String): LessonEntity?

    /**
     * Поиск урока по внешнему ID (для синхронизации).
     * Индекс: idx_lessons_external_id
     */
    @Query(
        """
        SELECT * FROM lessons
        WHERE external_id = :externalId
        """
    )
    suspend fun getByExternalId(externalId: String): LessonEntity?

    /**
     * Поиск уроков по ID цели обучения.
     * Индекс: idx_lessons_objective_id
     */
    @Query(
        """
        SELECT * FROM lessons
        WHERE primary_objective_id = :objectiveId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByObjective(objectiveId: String): Flow<List<LessonEntity>>

    /**
     * Получение уроков с ошибками (для повторения).
     * Возвращает уроки, в которых были допущены ошибки,
     * отсортированные по дате последнего прохождения.
     */
    @Query(
        """
        SELECT DISTINCT l.*
        FROM lessons l
        INNER JOIN lesson_completions lc
            ON l.id = lc.lesson_id
        WHERE lc.mistakes_count > 0 AND l.is_active = 1
        ORDER BY lc.completed_at DESC
        LIMIT :limit
        """
    )
    suspend fun getLessonsWithMistakes(limit: Int = 10): List<LessonEntity>

    /**
     * Получение диагностических уроков по ID темы.
     */
    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId
          AND is_diagnostic = 1
          AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeDiagnosticByTopic(topicId: String): Flow<List<LessonEntity>>

    /**
     * Получение бонусных уроков по ID темы.
     */
    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId
          AND is_bonus = 1
          AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeBonusByTopic(topicId: String): Flow<List<LessonEntity>>

    /**
     * Получение количества уроков в теме (для UI).
     */
    @Query(
        """
        SELECT COUNT(*) FROM lessons
        WHERE topic_id = :topicId AND is_active = 1
        """
    )
    suspend fun countByTopic(topicId: String): Int

    /**
     * Массовая вставка с заменой при конфликте.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<LessonEntity>)

    /**
     * Вставка или обновление одного урока.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lesson: LessonEntity)
}
