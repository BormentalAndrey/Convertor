package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    /**
     * Возвращает активные вопросы урока в случайном порядке.
     * Для режима тренировки.
     * Индекс: idx_questions_lesson_id
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE lesson_id = :lessonId AND is_active = 1
        ORDER BY RANDOM()
        """
    )
    fun observeByLessonRandom(lessonId: String): Flow<List<QuestionEntity>>

    /**
     * Возвращает активные вопросы урока в заданном порядке.
     * Для последовательного прохождения.
     * Индекс: idx_questions_lesson_sort (lesson_id, sort_order)
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE lesson_id = :lessonId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByLessonOrdered(lessonId: String): Flow<List<QuestionEntity>>

    /**
     * Возвращает все вопросы урока, включая неактивные.
     * Для сидирования и админки.
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE lesson_id = :lessonId
        ORDER BY sort_order ASC
        """
    )
    suspend fun getAllByLesson(lessonId: String): List<QuestionEntity>

    /**
     * Поиск вопроса по локальному ID.
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE id = :id
        """
    )
    suspend fun getById(id: String): QuestionEntity?

    /**
     * Поиск вопроса по внешнему ID (для синхронизации).
     * Индекс: idx_questions_external_id
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE external_id = :externalId
        """
    )
    suspend fun getByExternalId(externalId: String): QuestionEntity?

    /**
     * Возвращает вопросы по ID микро-навыка.
     * Критично для генерации упражнений на конкретный навык.
     * Индекс: idx_questions_skill_id
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE primary_skill_id = :skillId AND is_active = 1
        ORDER BY difficulty, sort_order ASC
        """
    )
    fun observeBySkill(skillId: String): Flow<List<QuestionEntity>>

    /**
     * Возвращает вопросы по типу внутри урока.
     * Индекс: idx_questions_lesson_type (lesson_id, question_type)
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE lesson_id = :lessonId
          AND question_type = :questionType
          AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByLessonAndType(
        lessonId: String,
        questionType: String
    ): Flow<List<QuestionEntity>>

    /**
     * Возвращает вопросы по диапазону сложности.
     * Для адаптивной генерации тестов.
     * Индекс: idx_questions_difficulty
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE difficulty BETWEEN :minDifficulty AND :maxDifficulty
          AND is_active = 1
        ORDER BY RANDOM()
        LIMIT :limit
        """
    )
    suspend fun getByDifficultyRange(
        minDifficulty: Int,
        maxDifficulty: Int,
        limit: Int
    ): List<QuestionEntity>

    /**
     * Подсчёт вопросов в уроке.
     */
    @Query(
        """
        SELECT COUNT(*) FROM questions
        WHERE lesson_id = :lessonId AND is_active = 1
        """
    )
    suspend fun countByLesson(lessonId: String): Int

    /**
     * Массовая вставка с заменой при конфликте.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<QuestionEntity>)

    /**
     * Вставка или обновление одного вопроса.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(question: QuestionEntity)
}
