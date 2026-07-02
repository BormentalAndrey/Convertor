package com.example.russianpath.data.exercise

import com.example.russianpath.core.exercise.*
import com.example.russianpath.core.knowledge.SkillCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DistractorGeneratorImpl @Inject constructor() : DistractorGenerator {

    override fun generate(
        correct: CorrectAnswer,
        count: Int,
        skillCode: SkillCode
    ): List<ExerciseOption> {
        val correctValue = when (correct) {
            is TextAnswer -> correct.value
            is ChoiceAnswer -> correct.value
            else -> return emptyList()
        }

        return when (skillCode) {
            SkillCode.COUNT_SYLLABLES,
            SkillCode.COUNT_LETTERS,
            SkillCode.COUNT_VOWELS,
            SkillCode.COUNT_CONSONANTS -> {
                val num = correctValue.toIntOrNull() ?: return emptyList()
                listOf(
                    TextOption("dist_1", (num - 1).coerceAtLeast(1).toString()),
                    TextOption("dist_2", (num + 1).toString()),
                    TextOption("dist_3", (num + 2).toString())
                ).take(count)
            }
            SkillCode.RECOGNIZE_SOFT_SIGN,
            SkillCode.RECOGNIZE_HARD_SIGN -> {
                val opposite = if (correctValue == "Да") "Нет" else "Да"
                listOf(TextOption("dist_1", opposite)).take(count)
            }
            else -> emptyList()
        }
    }
}
