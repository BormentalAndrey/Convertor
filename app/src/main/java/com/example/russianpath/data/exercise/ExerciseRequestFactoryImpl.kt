// app/src/main/java/com/example/russianpath/data/exercise/ExerciseRequestFactoryImpl.kt

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
            SkillCode.PHONETIC_ANALYSIS,
            SkillCode.ORTHOGRAPHIC_ANALYSIS,
            SkillCode.MORPHOLOGICAL_ANALYSIS,
            SkillCode.SYNTACTIC_ANALYSIS -> {
                if (Random.nextBoolean()) ExerciseType.CHOICE
                else ExerciseType.GAP_FILL
            }
            SkillCode.LEXICAL_MEANS,
            SkillCode.SPEECH_STYLES -> ExerciseType.CHOICE
            SkillCode.IDENTIFY_LANGUAGE_UNITS,
            SkillCode.MORPHEMIC_ANALYSIS -> ExerciseType.CHOICE
            SkillCode.PUNCTUATION_ANALYSIS -> ExerciseType.GAP_FILL
            else -> ExerciseType.CHOICE
        }
    }
}
