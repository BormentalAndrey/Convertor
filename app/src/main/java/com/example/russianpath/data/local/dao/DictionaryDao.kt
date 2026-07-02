package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {
    @Query("SELECT * FROM dictionary WHERE normalized = :normalized")
    suspend fun findByNormalized(normalized: String): DictionaryWordEntity?

    @Query("SELECT * FROM dictionary WHERE id = :id")
    suspend fun findById(id: String): DictionaryWordEntity?

    @Query("SELECT * FROM dictionary ORDER BY gradeLevel, difficulty")
    fun getAll(): Flow<List<DictionaryWordEntity>>

    @Query("SELECT * FROM dictionary ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandom(limit: Int): List<DictionaryWordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<DictionaryWordEntity>)
}
