// app/src/main/java/com/example/russianpath/data/local/dao/LessonDao.kt

package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId
        ORDER BY sort_order ASC
        """
    )
    fun observeByTopic(topicId: String): Flow<List<LessonEntity>>

    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId
        ORDER BY sort_order ASC
        """
    )
    suspend fun getAllByTopic(topicId: String): List<LessonEntity>

    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId
          AND lesson_type = :lessonType
        ORDER BY sort_order ASC
        """
    )
    fun observeByTopicAndType(topicId: String, lessonType: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getById(id: String): LessonEntity?

    @Query("SELECT * FROM lessons WHERE external_id = :externalId")
    suspend fun getByExternalId(externalId: String): LessonEntity?

    @Query(
        """
        SELECT * FROM lessons
        WHERE primary_objective_id = :objectiveId
        ORDER BY sort_order ASC
        """
    )
    fun observeByObjective(objectiveId: String): Flow<List<LessonEntity>>

    @Query(
        """
        SELECT DISTINCT l.*
        FROM lessons l
        INNER JOIN lesson_completions lc ON l.id = lc.lesson_id
        WHERE lc.mistakes_count > 0
        ORDER BY lc.completed_at DESC
        LIMIT :limit
        """
    )
    suspend fun getLessonsWithMistakes(limit: Int = 10): List<LessonEntity>

    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId AND is_diagnostic = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeDiagnosticByTopic(topicId: String): Flow<List<LessonEntity>>

    @Query(
        """
        SELECT * FROM lessons
        WHERE topic_id = :topicId AND is_bonus = 1
        ORDER BY sort_order ASC
        """
    )
    fun observeBonusByTopic(topicId: String): Flow<List<LessonEntity>>

    @Query("SELECT COUNT(*) FROM lessons WHERE topic_id = :topicId")
    suspend fun countByTopic(topicId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lesson: LessonEntity)
}
