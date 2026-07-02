package com.example.russianpath.data.repository

import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.core.knowledge.SkillCode
import com.example.russianpath.core.repository.WordRepository
import com.example.russianpath.data.local.dao.DictionaryDao
import com.example.russianpath.data.mapper.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val dictionaryDao: DictionaryDao
) : WordRepository {

    override suspend fun findByNormalized(normalized: String): DictionaryWord? {
        return dictionaryDao.findByNormalized(normalized)?.toDomain()
    }

    override suspend fun findBySkill(skillCode: SkillCode): List<DictionaryWord> {
        // Пока нет таблицы связи skill-dictionary, возвращаем слова по уровню
        return dictionaryDao.getRandom(20).map { it.toDomain() }
    }
}
