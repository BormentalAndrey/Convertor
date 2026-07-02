package com.example.russianpath.data.local.dao

import androidx.room.*
import com.example.russianpath.data.local.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Query("""
        SELECT * FROM topics_v2
        WHERE gradeLevel = :grade
        ORDER BY sortOrder
    """)
    fun getTopicsByGrade(grade: Int): Flow<List<TopicEntity>>

    @Query("""
        SELECT * FROM topics_v2
        ORDER BY gradeLevel, sortOrder
    """)
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("""
        UPDATE topics_v2
        SET isUnlocked = 1
        WHERE id = :id
    """)
    suspend fun unlockTopic(id: String)

    @Query("""
        SELECT * FROM topics_v2
        WHERE sectionId = :sectionId
        ORDER BY sortOrder
    """)
    fun observeBySection(sectionId: String): Flow<List<TopicEntity>>

    @Query("""
        SELECT * FROM topics_v2
        WHERE id = :id
    """)
    suspend fun getById(id: String): TopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicEntity>)
}
