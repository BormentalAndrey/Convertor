package com.example.russianpath.data.repository

import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.core.dictionary.WordTag
import com.example.russianpath.core.knowledge.SkillCode
import com.example.russianpath.core.repository.WordRepository
import com.example.russianpath.data.local.dao.DictionaryDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val dictionaryDao: DictionaryDao
) : WordRepository {

    private val gson = Gson()

    override suspend fun findByNormalized(normalized: String): DictionaryWord? {
        val entity = dictionaryDao.findByNormalized(normalized) ?: return null
        return entity.toDomain()
    }

    override suspend fun findBySkill(skillCode: SkillCode): List<DictionaryWord> {
        // В v1.0 возвращаем все слова нужного уровня сложности.
        // В v2.0 добавится таблица связей word_skills.
        val gradeLevel = when (skillCode.code / 1000) {
            1, 2 -> 1  // Графика и фонетика — 1 класс
            else -> 1
        }
        return dictionaryDao.getAll()
            // TODO: фильтрация по skillCode через таблицу связей (v2.0)
            .let { flow ->
                // Временно получаем все слова через getRandom
                dictionaryDao.getRandom(20).map { it.toDomain() }
            }
    }

    private fun DictionaryWordEntity.toDomain(): DictionaryWord {
        val tags = gson.fromJson<List<String>>(
            tagsJson,
            object : TypeToken<List<String>>() {}.type
        ).map { WordTag.valueOf(it) }.toSet()

        return DictionaryWord(
            id = id,
            word = word,
            normalized = normalized,
            gradeLevel = gradeLevel,
            difficulty = difficulty,
            stressPosition = stressPosition,
            syllables = syllablesJson?.let { json ->
                gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
            },
            tags = tags
        )
    }
}

// Нужен импорт для DictionaryWordEntity
import com.example.russianpath.data.local.entity.DictionaryWordEntity
