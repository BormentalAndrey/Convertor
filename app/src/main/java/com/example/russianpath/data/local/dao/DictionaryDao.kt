package com.example.russianpath.data.local.dao

import androidx.room.*
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {
    @Query("SELECT * FROM dictionary_words WHERE normalized = :normalized")
    suspend fun getByNormalized(normalized: String): DictionaryWordEntity?

    @Query("SELECT * FROM dictionary_words WHERE normalized = :normalized")
    fun observeByNormalized(normalized: String): Flow<DictionaryWordEntity?>

    @Query("SELECT * FROM dictionary_words WHERE id = :id")
    suspend fun getById(id: String): DictionaryWordEntity?

    @Query("SELECT * FROM dictionary_words ORDER BY gradeLevel, difficulty")
    fun observeAll(): Flow<List<DictionaryWordEntity>>

    @Query("SELECT * FROM dictionary_words ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandom(limit: Int): List<DictionaryWordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<DictionaryWordEntity>)
}
