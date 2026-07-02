package com.example.russianpath.core.exercise

import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.common.Difficulty  // ← исправлен
import com.example.russianpath.core.knowledge.SkillCode

data class ExerciseRequest(
    val skillCode: SkillCode,
    val exerciseType: ExerciseType,
    val difficulty: Difficulty,
    val analysis: WordAnalysis
)
