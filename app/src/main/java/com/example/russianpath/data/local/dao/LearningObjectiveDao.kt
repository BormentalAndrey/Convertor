package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.LearningObjectiveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningObjectiveDao {

    /**
     * Возвращает активные цели обучения по ID темы.
     * Основной запрос для отображения целей внутри темы.
     * Индекс: idx_objectives_topic_sort (topic_id, sort_order)
     */
    @Query(
        """
        SELECT * FROM learning_objectives
        WHERE topic_id = :topicId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByTopic(topicId: String): Flow<List<LearningObjectiveEntity>>

    /**
     * Возвращает все цели обучения по ID темы, включая неактивные.
     * Для сидирования и админки.
     */
    @Query(
        """
        SELECT * FROM learning_objectives
        WHERE topic_id = :topicId
        ORDER BY sort_order ASC
        """
    )
    suspend fun getAllByTopic(topicId: String): List<LearningObjectiveEntity>

    /**
     * Поиск цели обучения по локальному ID.
     */
    @Query(
        """
        SELECT * FROM learning_objectives
        WHERE id = :id
        """
    )
    suspend fun getById(id: String): LearningObjectiveEntity?

    /**
     * Поиск цели обучения по внешнему ID (для синхронизации).
     * Индекс: idx_objectives_external_id
     */
    @Query(
        """
        SELECT * FROM learning_objectives
        WHERE external_id = :externalId
        """
    )
    suspend fun getByExternalId(externalId: String): LearningObjectiveEntity?

    /**
     * Поиск целей обучения по коду навыка (из кодификатора).
     * Критично для ОГЭ/ЕГЭ: связь с номерами заданий экзамена.
     * Индекс: idx_objectives_skill_code
     */
    @Query(
        """
        SELECT * FROM learning_objectives
        WHERE skill_code_id = :skillCodeId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeBySkillCode(skillCodeId: Int): Flow<List<LearningObjectiveEntity>>

    /**
     * Массовая вставка с заменой при конфликте.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(objectives: List<LearningObjectiveEntity>)

    /**
     * Вставка или обновление одной цели.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(objective: LearningObjectiveEntity)

    /**
     * Подсчёт целей в теме (для проверки сидирования).
     */
    @Query(
        """
        SELECT COUNT(*) FROM learning_objectives
        WHERE topic_id = :topicId
        """
    )
    suspend fun countByTopic(topicId: String): Int

    /**
     * Получение пререквизитов цели обучения.
     */
    @Query(
        """
        SELECT prerequisite_objective_ids_json FROM learning_objectives
        WHERE id = :id
        """
    )
    suspend fun getPrerequisiteIds(id: String): String

    /**
     * Получение порога освоения для цели.
     */
    @Query(
        """
        SELECT mastery_threshold_percent FROM learning_objectives
        WHERE id = :id
        """
    )
    suspend fun getMasteryThreshold(id: String): Int
}
