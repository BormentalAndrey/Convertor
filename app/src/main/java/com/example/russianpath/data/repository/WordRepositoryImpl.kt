package com.example.russianpath.data.repository

import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.core.repository.WordCriteria
import com.example.russianpath.core.repository.WordRepository
import com.example.russianpath.data.local.dao.DictionaryDao
import com.example.russianpath.data.local.mapper.DictionaryWordMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val dictionaryDao: DictionaryDao,
    private val mapper: DictionaryWordMapper
) : WordRepository {

    override suspend fun findByNormalized(normalized: String): DictionaryWord? {
        return dictionaryDao.getByNormalized(normalized)?.let { mapper.toDomain(it) }
    }

    override suspend fun find(criteria: WordCriteria): List<DictionaryWord> {
        val entities = dictionaryDao.getAll(limit = criteria.limit)

        return entities
            .filter { entity ->
                criteria.gradeLevel?.let { entity.gradeLevel == it } ?: true
            }
            .filter { entity ->
                criteria.maxDifficulty?.let { entity.difficulty <= it.value } ?: true
            }
            .filter { entity ->
                if (criteria.tags.isEmpty()) true
                else {
                    val domain = mapper.toDomain(entity)
                    criteria.tags.any { it in domain.tags }
                }
            }
            .map { mapper.toDomain(it) }
    }
}
