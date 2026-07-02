package com.example.russianpath.core.exercise

import com.example.russianpath.core.knowledge.SkillCode

interface DistractorGenerator {
    fun generate(
        correct: CorrectAnswer,
        count: Int,
        skillCode: SkillCode
    ): List<ExerciseOption>
}
