package com.example.russianpath.data.mapper

import com.example.russianpath.core.dictionary.DictionaryWord
import com.example.russianpath.data.local.entity.DictionaryWordEntity

fun DictionaryWordEntity.toDomain(): DictionaryWord = DictionaryWord(
    id = id,
    word = word,
    normalized = normalized,
    stressPosition = stressPosition,
    hasSoftSign = word.contains('ь', ignoreCase = true),
    hasHardSign = word.contains('ъ', ignoreCase = true),
    difficulty = difficulty,
    gradeLevel = gradeLevel
)
