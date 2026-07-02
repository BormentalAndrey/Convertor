package com.example.russianpath.data.exercise

import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.exercise.*
import com.example.russianpath.core.knowledge.SkillCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateEngineImpl @Inject constructor() : TemplateEngine {

    override fun buildPrompt(request: ExerciseRequest): String {
        val word = request.analysis.dictionaryWord.word
        return when (request.skillCode) {
            SkillCode.COUNT_SYLLABLES -> "Сколько слогов в слове $word?"
            SkillCode.FIND_FIRST_LETTER -> "Какая первая буква в слове $word?"
            SkillCode.FIND_LAST_LETTER -> "Какая последняя буква в слове $word?"
            SkillCode.COUNT_LETTERS -> "Сколько букв в слове $word?"
            SkillCode.RECOGNIZE_SOFT_SIGN -> "Есть ли мягкий знак в слове $word?"
            SkillCode.RECOGNIZE_HARD_SIGN -> "Есть ли твёрдый знак в слове $word?"
            SkillCode.DIVIDE_TO_SYLLABLES -> "Раздели слово $word на слоги"
            SkillCode.FIND_STRESSED_SYLLABLE -> "Какой слог ударный в слове $word?"
            SkillCode.COUNT_VOWELS -> "Сколько гласных в слове $word?"
            SkillCode.COUNT_CONSONANTS -> "Сколько согласных в слове $word?"
            else -> "Задание для слова $word"
        }
    }

    override fun buildOptions(request: ExerciseRequest): List<ExerciseOption> {
        val analysis = request.analysis
        val correct = buildCorrectAnswer(request)
        val distractors = generateDistractors(correct, request.skillCode)
        
        val allOptions = (listOf(correct) + distractors).mapIndexed { index, answer ->
            TextOption(
                id = "opt_$index",
                text = when (answer) {
                    is TextAnswer -> answer.value
                    is ChoiceAnswer -> answer.value
                    else -> answer.toString()
                }
            )
        }
        return allOptions.shuffled()
    }

    override fun buildHint(request: ExerciseRequest): String {
        return when (request.skillCode) {
            SkillCode.COUNT_SYLLABLES -> "Сколько гласных, столько и слогов"
            SkillCode.FIND_FIRST_LETTER -> "Посмотри на первую букву слова"
            SkillCode.FIND_LAST_LETTER -> "Посмотри на последнюю букву слова"
            SkillCode.COUNT_LETTERS -> "Посчитай все буквы в слове"
            SkillCode.RECOGNIZE_SOFT_SIGN -> "Мягкий знак выглядит так: Ь"
            SkillCode.RECOGNIZE_HARD_SIGN -> "Твёрдый знак выглядит так: Ъ"
            SkillCode.DIVIDE_TO_SYLLABLES -> "Прохлопай слово по частям"
            SkillCode.FIND_STRESSED_SYLLABLE -> "Позови слово: МА-а-а-МА"
            SkillCode.COUNT_VOWELS -> "Гласные: А, О, У, Ы, Э, И, Е, Ё, Ю, Я"
            SkillCode.COUNT_CONSONANTS -> "Все буквы, кроме гласных, Ь и Ъ"
            else -> "Подумай внимательно"
        }
    }

    override fun buildCorrectAnswer(request: ExerciseRequest): CorrectAnswer {
        val analysis = request.analysis
        
        return when (request.skillCode) {
            SkillCode.COUNT_SYLLABLES -> {
                val count = analysis.syllableAnalysis?.count ?: 0
                TextAnswer(count.toString())
            }
            SkillCode.FIND_FIRST_LETTER -> {
                val letter = analysis.letterAnalysis.first
                TextAnswer(letter.toString().uppercase())
            }
            SkillCode.FIND_LAST_LETTER -> {
                val letter = analysis.letterAnalysis.last
                TextAnswer(letter.toString().uppercase())
            }
            SkillCode.COUNT_LETTERS -> {
                val count = analysis.letterAnalysis.count
                TextAnswer(count.toString())
            }
            SkillCode.RECOGNIZE_SOFT_SIGN -> {
                val answer = if (analysis.letterAnalysis.hasSoftSign) "Да" else "Нет"
                TextAnswer(answer)
            }
            SkillCode.RECOGNIZE_HARD_SIGN -> {
                val answer = if (analysis.letterAnalysis.hasHardSign) "Да" else "Нет"
                TextAnswer(answer)
            }
            SkillCode.COUNT_VOWELS -> {
                val count = analysis.letterAnalysis.letters.count { it.isVowel }
                TextAnswer(count.toString())
            }
            SkillCode.COUNT_CONSONANTS -> {
                val count = analysis.letterAnalysis.letters.count { it.isConsonant }
                TextAnswer(count.toString())
            }
            else -> TextAnswer("?")
        }
    }

    private fun generateDistractors(
        correct: CorrectAnswer,
        skillCode: SkillCode
    ): List<CorrectAnswer> {
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
                    TextAnswer((num - 1).coerceAtLeast(1).toString()),
                    TextAnswer((num + 1).toString()),
                    TextAnswer((num + 2).toString())
                )
            }
            SkillCode.RECOGNIZE_SOFT_SIGN,
            SkillCode.RECOGNIZE_HARD_SIGN -> {
                if (correctValue == "Да") {
                    listOf(TextAnswer("Нет"))
                } else {
                    listOf(TextAnswer("Да"))
                }
            }
            else -> emptyList()
        }
    }
}
