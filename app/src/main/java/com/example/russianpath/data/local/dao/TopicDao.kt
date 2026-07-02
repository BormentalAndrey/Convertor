package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics_v2 WHERE sectionId = :sectionId ORDER BY sortOrder")
    fun getBySection(sectionId: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics_v2 WHERE id = :id")
    suspend fun getById(id: String): TopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicEntity>)
}
