// app/src/main/java/com/example/russianpath/data/local/entity/DictionaryWordEntity.kt
package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val difficulty: Int,
    val tagsJson: String = "[]",
    val schemaVersion: Int = 1
)
