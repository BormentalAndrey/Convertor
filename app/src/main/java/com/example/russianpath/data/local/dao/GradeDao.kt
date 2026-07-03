package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.GradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {

    /**
     * Возвращает все активные классы, отсортированные по порядку.
     * Используется для отображения списка классов в UI.
     * Индекс: idx_grades_sort_order
     */
    @Query(
        """
        SELECT * FROM grades
        WHERE is_active = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeAll(): Flow<List<GradeEntity>>

    /**
     * Возвращает все классы, включая неактивные.
     * Для админских функций и сидирования.
     */
    @Query(
        """
        SELECT * FROM grades
        ORDER BY sort_order ASC
        """
    )
    suspend fun getAll(): List<GradeEntity>

    /**
     * Поиск класса по локальному ID.
     */
    @Query(
        """
        SELECT * FROM grades
        WHERE id = :id
        """
    )
    suspend fun getById(id: String): GradeEntity?

    /**
     * Поиск класса по внешнему ID (для синхронизации).
     * Индекс: idx_grades_external_id
     */
    @Query(
        """
        SELECT * FROM grades
        WHERE external_id = :externalId
        """
    )
    suspend fun getByExternalId(externalId: String): GradeEntity?

    /**
     * Массовая вставка с заменой при конфликте.
     * Используется при сидировании и синхронизации.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(grades: List<GradeEntity>)

    /**
     * Вставка или обновление одного класса.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(grade: GradeEntity)

    /**
     * Подсчёт количества классов (для проверки сидирования).
     */
    @Query("SELECT COUNT(*) FROM grades")
    suspend fun count(): Int
}
