package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.SectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {

    /**
     * Возвращает активные разделы класса, отсортированные по порядку.
     * Основной запрос для UI.
     * Индекс: idx_sections_grade_sort (grade_id, sort_order)
     */
    @Query(
        """
        SELECT * FROM sections
        WHERE grade_id = :gradeId AND is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeByGrade(gradeId: String): Flow<List<SectionEntity>>

    /**
     * Возвращает все разделы класса, включая неактивные.
     * Для сидирования и админки.
     */
    @Query(
        """
        SELECT * FROM sections
        WHERE grade_id = :gradeId
        ORDER BY sort_order ASC
        """
    )
    suspend fun getAllByGrade(gradeId: String): List<SectionEntity>

    /**
     * Поиск раздела по локальному ID.
     */
    @Query(
        """
        SELECT * FROM sections
        WHERE id = :id
        """
    )
    suspend fun getById(id: String): SectionEntity?

    /**
     * Поиск раздела по внешнему ID (для синхронизации).
     * Индекс: idx_sections_external_id
     */
    @Query(
        """
        SELECT * FROM sections
        WHERE external_id = :externalId
        """
    )
    suspend fun getByExternalId(externalId: String): SectionEntity?

    /**
     * Массовая вставка с заменой при конфликте.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sections: List<SectionEntity>)

    /**
     * Вставка или обновление одного раздела.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(section: SectionEntity)

    /**
     * Подсчёт разделов класса (для проверки сидирования).
     */
    @Query(
        """
        SELECT COUNT(*) FROM sections
        WHERE grade_id = :gradeId
        """
    )
    suspend fun countByGrade(gradeId: String): Int
}
