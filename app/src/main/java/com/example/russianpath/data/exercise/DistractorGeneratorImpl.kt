// app/src/main/java/com/example/russianpath/data/exercise/DistractorGeneratorImpl.kt

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
            SkillCode.PHONETIC_ANALYSIS,
            SkillCode.ORTHOGRAPHIC_ANALYSIS,
            SkillCode.MORPHOLOGICAL_ANALYSIS,
            SkillCode.SYNTACTIC_ANALYSIS -> {
                val num = value.toIntOrNull() ?: return emptyList()
                listOf(
                    TextOption(OptionId("d1"), (num - 1).coerceAtLeast(1).toString()),
                    TextOption(OptionId("d2"), (num + 1).toString()),
                    TextOption(OptionId("d3"), (num + 2).toString())
                ).take(count)
            }
            SkillCode.LEXICAL_MEANS,
            SkillCode.SPEECH_STYLES -> {
                val opposite = if (value == "Да") "Нет" else "Да"
                listOf(TextOption(OptionId("d1"), opposite)).take(count)
            }
            SkillCode.IDENTIFY_LANGUAGE_UNITS,
            SkillCode.MORPHEMIC_ANALYSIS -> {
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
