// app/src/main/java/com/example/russianpath/data/local/dao/QuestionDao.kt

package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE lesson_id = :lessonId ORDER BY sort_order ASC")
    fun observeByLessonOrdered(lessonId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE lesson_id = :lessonId ORDER BY RANDOM()")
    fun observeByLessonRandom(lessonId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE lesson_id = :lessonId ORDER BY sort_order ASC")
    suspend fun getAllByLesson(lessonId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getById(id: String): QuestionEntity?

    @Query("SELECT * FROM questions WHERE external_id = :externalId")
    suspend fun getByExternalId(externalId: String): QuestionEntity?

    @Query("SELECT * FROM questions WHERE primary_skill_id = :skillId ORDER BY difficulty, sort_order ASC")
    fun observeBySkill(skillId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE lesson_id = :lessonId AND question_type = :questionType ORDER BY sort_order ASC")
    fun observeByLessonAndType(lessonId: String, questionType: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE difficulty BETWEEN :minDifficulty AND :maxDifficulty ORDER BY RANDOM() LIMIT :limit")
    suspend fun getByDifficultyRange(minDifficulty: Int, maxDifficulty: Int, limit: Int): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM questions WHERE lesson_id = :lessonId")
    suspend fun countByLesson(lessonId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(question: QuestionEntity)
}
