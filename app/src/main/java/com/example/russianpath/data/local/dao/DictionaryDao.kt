package com.example.russianpath.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {

    /**
     * Поиск слова по нормализованной форме.
     * Индекс: idx_dictionary_normalized
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE normalized = :normalized AND is_active = 1
        """
    )
    suspend fun getByNormalized(normalized: String): DictionaryWordEntity?

    /**
     * Flow-версия поиска по нормализованной форме.
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE normalized = :normalized AND is_active = 1
        """
    )
    fun observeByNormalized(normalized: String): Flow<DictionaryWordEntity?>

    /**
     * Поиск слова по локальному ID.
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE id = :id
        """
    )
    suspend fun getById(id: String): DictionaryWordEntity?

    /**
     * Поиск слова по внешнему ID (для синхронизации).
     * Индекс: idx_dictionary_external_id
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE external_id = :externalId
        """
    )
    suspend fun getByExternalId(externalId: String): DictionaryWordEntity?

    /**
     * Возвращает все активные слова, отсортированные по классу и сложности.
     * Для полного словаря.
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE is_active = 1
        ORDER BY grade_id, difficulty, id
        """
    )
    fun observeAll(): Flow<List<DictionaryWordEntity>>

    /**
     * Возвращает ограниченный список слов.
     * Для пагинации и предпросмотра.
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE is_active = 1
        ORDER BY grade_id, difficulty, id
        LIMIT :limit
        """
    )
    suspend fun getAll(limit: Int): List<DictionaryWordEntity>

    /**
     * Возвращает слова с пагинацией (offset + limit).
     * Для ленивой загрузки словаря.
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE is_active = 1
        ORDER BY grade_id, difficulty, id
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getPaged(limit: Int, offset: Int): List<DictionaryWordEntity>

    /**
     * Поиск слов по ID класса и сложности.
     * Основной запрос для учебного словаря.
     * Индекс: idx_dictionary_active_grade_difficulty
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE grade_id = :gradeId
          AND difficulty <= :maxDifficulty
          AND is_active = 1
        ORDER BY difficulty, frequency_rank, id
        """
    )
    fun observeByGradeAndDifficulty(
        gradeId: String,
        maxDifficulty: Int
    ): Flow<List<DictionaryWordEntity>>

    /**
     * Поиск слов по части речи.
     * Индекс: idx_dictionary_part_of_speech
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE part_of_speech = :partOfSpeech AND is_active = 1
        ORDER BY difficulty, id
        """
    )
    fun observeByPartOfSpeech(partOfSpeech: String): Flow<List<DictionaryWordEntity>>

    /**
     * Поиск словарных слов (требующих запоминания).
     * Для интервального повторения.
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE is_vocabulary_word = 1 AND is_active = 1
        ORDER BY difficulty, id
        """
    )
    fun observeVocabularyWords(): Flow<List<DictionaryWordEntity>>

    /**
     * Поиск слов-исключений.
     * Для специальных упражнений.
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE is_exception = 1 AND is_active = 1
        ORDER BY grade_id, difficulty, id
        """
    )
    fun observeExceptionWords(): Flow<List<DictionaryWordEntity>>

    /**
     * Полнотекстовый поиск по слову (LIKE).
     * Для поиска в UI.
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE (word LIKE '%' || :query || '%'
               OR normalized LIKE '%' || :query || '%')
          AND is_active = 1
        ORDER BY frequency_rank, id
        LIMIT :limit
        """
    )
    suspend fun search(query: String, limit: Int = 50): List<DictionaryWordEntity>

    /**
     * Получение слов по ID правила орфографии.
     * Для привязки слов к правилам.
     */
    @Query(
        """
        SELECT * FROM dictionary_words
        WHERE spelling_rule_id = :ruleId AND is_active = 1
        ORDER BY difficulty, id
        """
    )
    fun observeBySpellingRule(ruleId: String): Flow<List<DictionaryWordEntity>>

    /**
     * Массовая вставка с заменой при конфликте.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<DictionaryWordEntity>)

    /**
     * Вставка или обновление одного слова.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(word: DictionaryWordEntity)

    /**
     * Подсчёт всех слов в словаре.
     */
    @Query("SELECT COUNT(*) FROM dictionary_words WHERE is_active = 1")
    suspend fun count(): Int

    /**
     * Подсчёт слов по классу.
     */
    @Query(
        """
        SELECT COUNT(*) FROM dictionary_words
        WHERE grade_id = :gradeId AND is_active = 1
        """
    )
    suspend fun countByGrade(gradeId: String): Int
}
