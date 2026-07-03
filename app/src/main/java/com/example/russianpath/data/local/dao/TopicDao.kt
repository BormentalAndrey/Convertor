package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    /**
     * Возвращает активные и разблокированные темы по ID класса.
     * Основной запрос DashboardViewModel.
     * Индекс: idx_topics_grade_sort (grade_id, sort_order)
     */
    @Query(
        """
        SELECT * FROM topics
        WHERE grade_id = :gradeId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByGrade(gradeId: String): Flow<List<TopicEntity>>

    /**
     * Возвращает все темы (активные и неактивные) по ID класса.
     * Для карты контента и админки.
     */
    @Query(
        """
        SELECT * FROM topics
        WHERE grade_id = :gradeId
        ORDER BY sort_order ASC
        """
    )
    suspend fun getAllByGrade(gradeId: String): List<TopicEntity>

    /**
     * Возвращает активные и разблокированные темы по ID раздела.
     * Индекс: idx_topics_section_sort (section_id, sort_order)
     */
    @Query(
        """
        SELECT * FROM topics
        WHERE section_id = :sectionId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeBySection(sectionId: String): Flow<List<TopicEntity>>

    /**
     * Возвращает все темы по ID раздела, включая неактивные.
     */
    @Query(
        """
        SELECT * FROM topics
        WHERE section_id = :sectionId
        ORDER BY sort_order ASC
        """
    )
    suspend fun getAllBySection(sectionId: String): List<TopicEntity>

    /**
     * Возвращает все темы, отсортированные по классу и порядку.
     * Для полной карты контента.
     */
    @Query(
        """
        SELECT * FROM topics
        ORDER BY grade_id, sort_order ASC
        """
    )
    fun observeAll(): Flow<List<TopicEntity>>

    /**
     * Поиск темы по локальному ID.
     */
    @Query(
        """
        SELECT * FROM topics
        WHERE id = :id
        """
    )
    suspend fun getById(id: String): TopicEntity?

    /**
     * Поиск темы по внешнему ID (для синхронизации).
     * Индекс: idx_topics_external_id
     */
    @Query(
        """
        SELECT * FROM topics
        WHERE external_id = :externalId
        """
    )
    suspend fun getByExternalId(externalId: String): TopicEntity?

    /**
     * Разблокировка темы.
     */
    @Query(
        """
        UPDATE topics
        SET is_unlocked = 1
        WHERE id = :id
        """
    )
    suspend fun unlockTopic(id: String)

    /**
     * Проверка, разблокирована ли тема.
     */
    @Query(
        """
        SELECT is_unlocked FROM topics
        WHERE id = :id
        """
    )
    suspend fun isTopicUnlocked(id: String): Boolean

    /**
     * Получение пререквизитов темы (ID тем, которые нужно пройти перед этой).
     */
    @Query(
        """
        SELECT prerequisite_topic_ids_json FROM topics
        WHERE id = :id
        """
    )
    suspend fun getPrerequisiteIds(id: String): String

    /**
     * Массовая вставка с заменой при конфликте.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicEntity>)

    /**
     * Вставка или обновление одной темы.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(topic: TopicEntity)

    /**
     * Подсчёт тем класса (для проверки сидирования).
     */
    @Query(
        """
        SELECT COUNT(*) FROM topics
        WHERE grade_id = :gradeId
        """
    )
    suspend fun countByGrade(gradeId: String): Int

    /**
     * Поиск тем по контент-хешу (для инкрементальной синхронизации).
     */
    @Query(
        """
        SELECT * FROM topics
        WHERE content_hash = :hash
        """
    )
    suspend fun getByContentHash(hash: String): List<TopicEntity>
}
