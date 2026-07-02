package com.example.russianpath.core.repository

import com.example.russianpath.core.dictionary.DictionaryWord

interface WordRepository {
    suspend fun findByNormalized(normalized: String): DictionaryWord?
    suspend fun find(criteria: WordCriteria): List<DictionaryWord>
}
