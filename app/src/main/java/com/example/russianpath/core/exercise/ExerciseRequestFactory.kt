package com.example.russianpath.core.exercise

import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.knowledge.SkillCode

interface ExerciseRequestFactory {
    fun createRequest(
        skillCode: SkillCode,
        analysis: WordAnalysis,
        difficulty: Difficulty
    ): ExerciseRequest
}
