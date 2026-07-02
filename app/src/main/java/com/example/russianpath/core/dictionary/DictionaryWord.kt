package com.example.russianpath.core.dictionary

import com.example.russianpath.core.common.Difficulty  // ← исправлен

data class DictionaryWord(
    val id: WordId,
    val word: String,
    val normalized: String,
    val gradeLevel: Int,
    val difficulty: Difficulty,
    val tags: Set<WordTag> = emptySet(),
    val schemaVersion: Int = 1
)
