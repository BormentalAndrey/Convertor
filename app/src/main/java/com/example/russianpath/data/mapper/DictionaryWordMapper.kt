// app/src/main/java/com/example/russianpath/data/local/mapper/DictionaryWordMapper.kt
package com.example.russianpath.data.local.mapper

import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.core.dictionary.WordId
import com.example.russianpath.data.local.entity.DictionaryWordEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryWordMapper @Inject constructor() {

    fun toDomain(entity: DictionaryWordEntity): DictionaryWord {
        return DictionaryWord(
            id = WordId(entity.id),
            word = entity.word,
            normalized = entity.normalized,
            gradeLevel = entity.gradeLevel,
            difficulty = entity.difficulty,  // TypeConverter сработает в Room
            tags = entity.tags,              // TypeConverter сработает в Room
            schemaVersion = entity.schemaVersion
        )
    }

    fun toEntity(domain: DictionaryWord): DictionaryWordEntity {
        return DictionaryWordEntity(
            id = domain.id.value,
            word = domain.word,
            normalized = domain.normalized,
            gradeLevel = domain.gradeLevel,
            difficulty = domain.difficulty,
            tags = domain.tags,
            schemaVersion = domain.schemaVersion
        )
    }
}
