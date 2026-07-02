package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionary")
data class DictionaryWordEntity(
    @PrimaryKey
    val id: String,
    val word: String,
    val normalized: String,
    val gradeLevel: Int,
    val difficulty: Int,
    val stressPosition: Int?,
    val syllablesJson: String?,
    val tagsJson: String = "[]",
    val schemaVersion: Int = 1
)
