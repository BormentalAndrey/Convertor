package com.example.russianpath.data.exercise

import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.common.Difficulty
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
        return ExerciseRequest(
            skillCode = skillCode,
            exerciseType = selectExerciseType(skillCode),
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
                if (Random.nextBoolean()) ExerciseType.CHOICE
                else ExerciseType.GAP_FILL
            }
            SkillCode.RECOGNIZE_SOFT_SIGN,
            SkillCode.RECOGNIZE_HARD_SIGN -> ExerciseType.CHOICE
            SkillCode.FIND_FIRST_LETTER,
            SkillCode.FIND_LAST_LETTER -> ExerciseType.CHOICE
            SkillCode.DIVIDE_TO_SYLLABLES -> ExerciseType.GAP_FILL
            else -> ExerciseType.CHOICE
        }
    }
}
