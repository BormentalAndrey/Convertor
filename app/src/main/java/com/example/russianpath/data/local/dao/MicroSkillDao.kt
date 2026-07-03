package com.example.russianpath.data.local.dao

import androidx.room.*
import com.example.russianpath.data.local.entity.MicroSkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MicroSkillDao {

    @Query("SELECT * FROM micro_skills WHERE objectiveId = :objectiveId")
    fun observeByObjective(objectiveId: String): Flow<List<MicroSkillEntity>>

    @Query("SELECT * FROM micro_skills WHERE id = :id")
    suspend fun getById(id: String): MicroSkillEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skills: List<MicroSkillEntity>)
}
