package com.example.russianpath.data.local.dao

import androidx.room.*
import com.example.russianpath.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE lessonId = :lessonId ORDER BY RANDOM()")
    fun getQuestionsByLesson(lessonId: String): Flow<List<QuestionEntity>>
    
    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: String): QuestionEntity?
}
