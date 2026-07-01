package com.example.russianpath.data.local.dao

import androidx.room.*
import com.example.russianpath.data.local.entity.UserProgressEntity
import com.example.russianpath.data.local.entity.LessonCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgressEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProgress(progress: UserProgressEntity)
    
    @Query("UPDATE user_progress SET totalXp = totalXp + :xp WHERE id = 1")
    suspend fun addXp(xp: Int)
    
    @Query("UPDATE user_progress SET gemsBalance = gemsBalance + :gems WHERE id = 1")
    suspend fun addGems(gems: Int)
    
    @Query("UPDATE user_progress SET livesCount = :lives WHERE id = 1")
    suspend fun updateLives(lives: Int)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLessonCompletion(completion: LessonCompletionEntity)
    
    @Query("SELECT * FROM lesson_completion WHERE lessonId = :lessonId")
    suspend fun getLessonCompletion(lessonId: String): LessonCompletionEntity?
}
