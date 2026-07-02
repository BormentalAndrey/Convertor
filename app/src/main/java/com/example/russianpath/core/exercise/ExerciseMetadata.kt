package com.example.russianpath.core.exercise

import com.example.russianpath.core.knowledge.SkillCode

data class ExerciseMetadata(
    val skillCode: SkillCode,
    val exerciseType: ExerciseType,
    val presentationType: PresentationType,
    val difficulty: Difficulty,
    val dictionaryWordId: String
)
