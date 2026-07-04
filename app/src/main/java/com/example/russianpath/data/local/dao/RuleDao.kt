// app/src/main/java/com/example/russianpath/data/local/dao/RuleDao.kt

package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.RuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {

    /**
     * Возвращает все правила по ID темы.
     */
    @Query(
        """
        SELECT * FROM rules
        WHERE topic_id = :topicId
        ORDER BY sort_order ASC
        """
    )
    fun observeByTopic(topicId: String): Flow<List<RuleEntity>>

    /**
     * Возвращает все правила по ID класса.
     */
    @Query(
        """
        SELECT * FROM rules
        WHERE grade_id = :gradeId
        ORDER BY sort_order ASC
        """
    )
    fun observeByGrade(gradeId: String): Flow<List<RuleEntity>>

    /**
     * Возвращает правила по категории внутри класса.
     */
    @Query(
        """
        SELECT * FROM rules
        WHERE rule_category = :category AND grade_id = :gradeId
        ORDER BY difficulty_level, sort_order ASC
        """
    )
    fun observeByCategoryAndGrade(category: String, gradeId: String): Flow<List<RuleEntity>>

    /**
     * Поиск правила по ID.
     */
    @Query("SELECT * FROM rules WHERE id = :id")
    suspend fun getById(id: String): RuleEntity?

    /**
     * Поиск правила по внешнему ID.
     */
    @Query("SELECT * FROM rules WHERE external_id = :externalId")
    suspend fun getByExternalId(externalId: String): RuleEntity?

    /**
     * Полнотекстовый поиск по названию и тексту правила.
     */
    @Query(
        """
        SELECT * FROM rules
        WHERE (title LIKE '%' || :query || '%' OR rule_text LIKE '%' || :query || '%')
        ORDER BY grade_id, sort_order ASC
        LIMIT :limit
        """
    )
    suspend fun search(query: String, limit: Int = 20): List<RuleEntity>

    /**
     * Подсчёт правил в теме.
     */
    @Query("SELECT COUNT(*) FROM rules WHERE topic_id = :topicId")
    suspend fun countByTopic(topicId: String): Int

    /**
     * Массовая вставка.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<RuleEntity>)

    /**
     * Вставка или обновление одного правила.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rule: RuleEntity)
}
