package com.example.russianpath.data.local.dao

import androidx.room.*
import com.example.russianpath.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE topicId = :topicId ORDER BY sortOrder")
    fun getLessonsByTopic(topicId: String): Flow<List<LessonEntity>>
    
    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: String): LessonEntity?
    
    @Query("""
        SELECT l.* FROM lessons l
        INNER JOIN lesson_completion lc ON l.id = lc.lessonId
        WHERE lc.mistakesCount > 0
        ORDER BY lc.completedAt DESC
        LIMIT 10
    """)
    suspend fun getLessonsWithMistakes(): List<LessonEntity>
}
