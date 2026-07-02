package com.example.russianpath.core.repository

import com.example.russianpath.core.common.Difficulty  // ← исправлен
import com.example.russianpath.core.dictionary.WordTag

data class WordCriteria(
    val gradeLevel: Int? = null,
    val maxDifficulty: Difficulty? = null,
    val tags: Set<WordTag> = emptySet(),
    val limit: Int = 10
)
