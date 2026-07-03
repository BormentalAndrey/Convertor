package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("""
        SELECT *
        FROM questions
        WHERE lessonId = :lessonId
        ORDER BY RANDOM()
    """)
    fun getQuestionsByLesson(lessonId: String): Flow<List<QuestionEntity>>

    @Query("""
        SELECT *
        FROM questions
        WHERE id = :id
    """)
    suspend fun getQuestionById(id: String): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<QuestionEntity>)
}
