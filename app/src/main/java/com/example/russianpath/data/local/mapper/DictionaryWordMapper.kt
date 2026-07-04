// app/src/main/java/com/example/russianpath/data/local/mapper/DictionaryWordMapper.kt

package com.example.russianpath.data.local.mapper

import com.example.russianpath.core.common.Difficulty
import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.core.dictionary.WordId
import com.example.russianpath.core.dictionary.WordIdFactory
import com.example.russianpath.core.dictionary.WordTag
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryWordMapper @Inject constructor() {

    fun toDomain(entity: DictionaryWordEntity): DictionaryWord {
        val tags = entity.tagsJson
            .trim('[', ']')
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { tag ->
                try {
                    WordTag.valueOf(tag.trim().trim('"'))
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            .toSet()

        return DictionaryWord(
            id = WordIdFactory.fromNormalized(entity.normalized),
            word = entity.word,
            normalized = entity.normalized,
            gradeLevel = entity.gradeId.toIntOrNull() ?: 0,
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
            gradeId = domain.gradeLevel.toString(),
            difficulty = domain.difficulty.value,
            tagsJson = domain.tags.joinToString(",") { it.name },
            schemaVersion = domain.schemaVersion
        )
    }
}
