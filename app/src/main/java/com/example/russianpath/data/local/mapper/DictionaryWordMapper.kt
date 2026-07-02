package com.example.russianpath.data.local.mapper

import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.core.dictionary.WordTag
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryWordMapper @Inject constructor() {

    private val gson = Gson()

    fun toDomain(entity: DictionaryWordEntity): DictionaryWord {
        val tags = gson.fromJson<List<String>>(
            entity.tagsJson,
            object : TypeToken<List<String>>() {}.type
        ).map { WordTag.valueOf(it) }.toSet()

        val syllables = entity.syllablesJson?.let { json ->
            gson.fromJson<List<String>>(
                json,
                object : TypeToken<List<String>>() {}.type
            )
        }

        return DictionaryWord(
            id = entity.id,
            word = entity.word,
            normalized = entity.normalized,
            gradeLevel = entity.gradeLevel,
            difficulty = entity.difficulty,
            stressPosition = entity.stressPosition,
            syllables = syllables,
            tags = tags,
            schemaVersion = entity.schemaVersion
        )
    }
}
