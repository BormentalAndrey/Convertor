// app/src/main/java/com/example/russianpath/data/local/mapper/DictionaryWordMapper.kt
package com.example.russianpath.data.local.mapper

import com.example.russianpath.core.common.Difficulty
import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.core.dictionary.WordId
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
        val tags = parseTags(entity.tagsJson)

        return DictionaryWord(
            id = WordId(entity.id),
            word = entity.word,
            normalized = entity.normalized,
            gradeLevel = entity.gradeLevel,
            difficulty = Difficulty(entity.difficulty),
            tags = tags,
            schemaVersion = entity.schemaVersion
        )
    }

    fun toEntity(domain: DictionaryWord): DictionaryWordEntity {
        return DictionaryWordEntity(
            id = domain.id.value,
            word = domain.word,
            normalized = domain.normalized,
            gradeLevel = domain.gradeLevel,
            difficulty = domain.difficulty.value,
            tagsJson = tagsToJson(domain.tags),
            schemaVersion = domain.schemaVersion
        )
    }

    private fun parseTags(json: String): Set<WordTag> {
        if (json.isBlank() || json == "[]") return emptySet()
        val type = object : TypeToken<List<String>>() {}.type
        val names: List<String> = gson.fromJson(json, type)
        return names.map { WordTag.valueOf(it) }.toSet()
    }

    private fun tagsToJson(tags: Set<WordTag>): String {
        return gson.toJson(tags.map { it.name })
    }
}
