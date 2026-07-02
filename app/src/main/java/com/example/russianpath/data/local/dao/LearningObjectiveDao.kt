package com.example.russianpath.data.local.dao

import androidx.room.*
import com.example.russianpath.data.local.entity.LearningObjectiveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningObjectiveDao {
    @Query("SELECT * FROM learning_objectives WHERE topicId = :topicId ORDER BY sortOrder")
    fun observeByTopic(topicId: String): Flow<List<LearningObjectiveEntity>>

    @Query("SELECT * FROM learning_objectives WHERE id = :id")
    suspend fun getById(id: String): LearningObjectiveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(objectives: List<LearningObjectiveEntity>)
}
