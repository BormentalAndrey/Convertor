package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.MicroSkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MicroSkillDao {

    /**
     * Возвращает активные микро-навыки по ID цели обучения.
     * Индекс: idx_micro_skills_objective_sort (objective_id, sort_order)
     */
    @Query(
        """
        SELECT * FROM micro_skills
        WHERE objective_id = :objectiveId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByObjective(objectiveId: String): Flow<List<MicroSkillEntity>>

    /**
     * Возвращает все микро-навыки по ID цели, включая неактивные.
     * Для сидирования.
     */
    @Query(
        """
        SELECT * FROM micro_skills
        WHERE objective_id = :objectiveId
        ORDER BY sort_order ASC
        """
    )
    suspend fun getAllByObjective(objectiveId: String): List<MicroSkillEntity>

    /**
     * Поиск микро-навыка по локальному ID.
     */
    @Query(
        """
        SELECT * FROM micro_skills
        WHERE id = :id
        """
    )
    suspend fun getById(id: String): MicroSkillEntity?

    /**
     * Поиск микро-навыка по внешнему ID (для синхронизации).
     * Индекс: idx_micro_skills_external_id
     */
    @Query(
        """
        SELECT * FROM micro_skills
        WHERE external_id = :externalId
        """
    )
    suspend fun getByExternalId(externalId: String): MicroSkillEntity?

    /**
     * Поиск микро-навыков по коду навыка (кодификатор).
     * Индекс: idx_micro_skills_skill_code
     */
    @Query(
        """
        SELECT * FROM micro_skills
        WHERE skill_code_id = :skillCodeId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeBySkillCode(skillCodeId: Int): Flow<List<MicroSkillEntity>>

    /**
     * Поиск дочерних микро-навыков по родительскому ID.
     * Для построения дерева навыков.
     */
    @Query(
        """
        SELECT * FROM micro_skills
        WHERE parent_micro_skill_id = :parentId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByParent(parentId: String): Flow<List<MicroSkillEntity>>

    /**
     * Поиск корневых микро-навыков (без родителя) для цели обучения.
     */
    @Query(
        """
        SELECT * FROM micro_skills
        WHERE objective_id = :objectiveId
          AND parent_micro_skill_id = ''
          AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeRootsByObjective(objectiveId: String): Flow<List<MicroSkillEntity>>

    /**
     * Поиск микро-навыков по категории ошибок.
     * Критично для системы аналитики ошибок.
     */
    @Query(
        """
        SELECT * FROM micro_skills
        WHERE error_category = :errorCategory AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByErrorCategory(errorCategory: String): Flow<List<MicroSkillEntity>>

    /**
     * Получение типичных ошибочных паттернов для навыка.
     * Используется для предсказания ошибок и генерации упражнений.
     */
    @Query(
        """
        SELECT typical_mistake_pattern_json FROM micro_skills
        WHERE id = :id
        """
    )
    suspend fun getMistakePatterns(id: String): String

    /**
     * Массовая вставка с заменой при конфликте.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skills: List<MicroSkillEntity>)

    /**
     * Вставка или обновление одного навыка.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(skill: MicroSkillEntity)

    /**
     * Подсчёт навыков в цели обучения.
     */
    @Query(
        """
        SELECT COUNT(*) FROM micro_skills
        WHERE objective_id = :objectiveId
        """
    )
    suspend fun countByObjective(objectiveId: String): Int
}
