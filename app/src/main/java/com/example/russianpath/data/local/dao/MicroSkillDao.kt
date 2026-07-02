package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.MicroSkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MicroSkillDao {
    @Query("SELECT * FROM micro_skills WHERE objectiveId = :objectiveId")
    fun getByObjective(objectiveId: String): Flow<List<MicroSkillEntity>>

    @Query("SELECT * FROM micro_skills WHERE id = :id")
    suspend fun getById(id: String): MicroSkillEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skills: List<MicroSkillEntity>)
}
