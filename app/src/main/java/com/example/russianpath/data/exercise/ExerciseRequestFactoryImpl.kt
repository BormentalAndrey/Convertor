package com.example.russianpath.data.exercise

import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.exercise.*
import com.example.russianpath.core.knowledge.SkillCode
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ExerciseRequestFactoryImpl @Inject constructor() : ExerciseRequestFactory {

    override fun createRequest(
        skillCode: SkillCode,
        analysis: WordAnalysis,
        difficulty: Difficulty
    ): ExerciseRequest {
        val exerciseType = selectExerciseType(skillCode)
        
        return ExerciseRequest(
            skillCode = skillCode,
            exerciseType = exerciseType,
            difficulty = difficulty,
            analysis = analysis
        )
    }

    private fun selectExerciseType(skillCode: SkillCode): ExerciseType {
        return when (skillCode) {
            SkillCode.COUNT_SYLLABLES,
            SkillCode.COUNT_LETTERS,
            SkillCode.COUNT_VOWELS,
            SkillCode.COUNT_CONSONANTS -> {
                // Можно задавать вопрос с выбором или с вводом числа
                if (Random.nextBoolean()) ExerciseType.CHOICE
                else ExerciseType.GAP_FILL
            }
            SkillCode.RECOGNIZE_SOFT_SIGN,
            SkillCode.RECOGNIZE_HARD_SIGN -> ExerciseType.CHOICE
            else -> ExerciseType.CHOICE
        }
    }
}
