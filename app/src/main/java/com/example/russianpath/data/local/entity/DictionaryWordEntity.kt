// app/src/main/java/com/example/russianpath/data/local/entity/DictionaryWordEntity.kt
package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.russianpath.core.common.Difficulty
import com.example.russianpath.core.dictionary.WordTag

@Entity(
    tableName = "dictionary_words",
    indices = [
        Index("normalized"),
        Index("gradeLevel")
    ]
)
data class DictionaryWordEntity(
    @PrimaryKey
    val id: String,
    val word: String,
    val normalized: String,
    val gradeLevel: Int,
    val difficulty: Difficulty,
    val tags: Set<WordTag> = emptySet(),
    val schemaVersion: Int = 1
)
