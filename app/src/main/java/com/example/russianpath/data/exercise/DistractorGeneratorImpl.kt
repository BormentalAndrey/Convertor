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
        val value = when (correct) {
            is TextAnswer -> correct.value
            is ChoiceAnswer -> correct.value
        }

        return when (skillCode) {
            SkillCode.COUNT_SYLLABLES,
            SkillCode.COUNT_LETTERS,
            SkillCode.COUNT_VOWELS,
            SkillCode.COUNT_CONSONANTS -> {
                val num = value.toIntOrNull() ?: return emptyList()
                listOf(
                    TextOption(OptionId("d1"), (num - 1).coerceAtLeast(1).toString()),
                    TextOption(OptionId("d2"), (num + 1).toString()),
                    TextOption(OptionId("d3"), (num + 2).toString())
                ).take(count)
            }
            SkillCode.RECOGNIZE_SOFT_SIGN,
            SkillCode.RECOGNIZE_HARD_SIGN -> {
                val opposite = if (value == "Да") "Нет" else "Да"
                listOf(TextOption(OptionId("d1"), opposite)).take(count)
            }
            SkillCode.FIND_FIRST_LETTER,
            SkillCode.FIND_LAST_LETTER -> {
                val chars = listOf("А", "О", "У", "И", "Е")
                    .filter { it != value }
                    .take(count)
                    .mapIndexed { i, c -> TextOption(OptionId("d$i"), c) }
                chars
            }
            else -> emptyList()
        }
    }
}
