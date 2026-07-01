package com.example.russianpath.data.local.dao

import androidx.room.*
import com.example.russianpath.data.local.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE gradeLevel = :grade ORDER BY sortOrder")
    fun getTopicsByGrade(grade: Int): Flow<List<TopicEntity>>
    
    @Query("SELECT * FROM topics ORDER BY sortOrder")
    fun getAllTopics(): Flow<List<TopicEntity>>
    
    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: String): TopicEntity?
    
    @Update
    suspend fun updateTopic(topic: TopicEntity)
    
    @Query("UPDATE topics SET isUnlocked = 1 WHERE id = :id")
    suspend fun unlockTopic(id: String)
}
