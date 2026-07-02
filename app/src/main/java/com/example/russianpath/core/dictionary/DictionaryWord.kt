package com.example.russianpath.core.dictionary

data class DictionaryWord(
    val id: String,
    val word: String,
    val normalized: String,
    val gradeLevel: Int,
    val difficulty: Int,
    val stressPosition: Int?,
    val syllables: List<String>?,
    val tags: Set<WordTag> = emptySet(),
    val schemaVersion: Int = 1
)
