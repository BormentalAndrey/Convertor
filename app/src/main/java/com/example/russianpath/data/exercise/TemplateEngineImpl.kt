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
            SkillCode.COUNT_SYLLABLES -> "Сколько слогов в слове «$word»?"
            SkillCode.DIVIDE_TO_SYLLABLES -> "Раздели слово «$word» на слоги"
            SkillCode.FIND_STRESSED_SYLLABLE -> "Какой слог ударный в слове «$word»?"
            SkillCode.COUNT_VOWELS -> "Сколько гласных в слове «$word»?"
            SkillCode.COUNT_CONSONANTS -> "Сколько согласных в слове «$word»?"
            SkillCode.FIND_FIRST_LETTER -> "Какая первая буква в слове «$word»?"
            SkillCode.FIND_LAST_LETTER -> "Какая последняя буква в слове «$word»?"
            SkillCode.COUNT_LETTERS -> "Сколько букв в слове «$word»?"
            SkillCode.RECOGNIZE_SOFT_SIGN -> "Есть ли мягкий знак (ь) в слове «$word»?"
            SkillCode.RECOGNIZE_HARD_SIGN -> "Есть ли твёрдый знак (ъ) в слове «$word»?"
            SkillCode.SPELLING_ZHI_SHI -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.SPELLING_CHA_SCHA -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.SPELLING_CHU_SCHU -> "Вставь пропущенную букву в слово «$word»"
            SkillCode.FIND_ROOT -> "Найди корень в слове «$word»"
            SkillCode.FIND_PREFIX -> "Найди приставку в слове «$word»"
            SkillCode.FIND_SUFFIX -> "Найди суффикс в слове «$word»"
            SkillCode.FIND_ENDING -> "Найди окончание в слове «$word»"
        }
    }

    override fun buildOptions(request: ExerciseRequest): List<ExerciseOption> {
        val correct = buildCorrectAnswer(request)
        val distractors = generateDistractorsFor(correct, request.skillCode)

        val allOptions = (listOf(correct) + distractors)
            .mapIndexed { index, answer ->
                val text = when (answer) {
                    is TextAnswer -> answer.value
                    is ChoiceAnswer -> answer.value
                }
                TextOption(id = OptionId("opt_$index"), text = text)
            }
            .shuffled()

        return allOptions
    }

    override fun buildHint(request: ExerciseRequest): String {
        return when (request.skillCode) {
            SkillCode.COUNT_SYLLABLES -> "Сколько гласных, столько и слогов"
            SkillCode.DIVIDE_TO_SYLLABLES -> "Прохлопай слово по частям"
            SkillCode.FIND_STRESSED_SYLLABLE -> "Позови слово: МА-а-а-МА"
            SkillCode.COUNT_VOWELS -> "Гласные: А, О, У, Ы, Э, И, Е, Ё, Ю, Я"
            SkillCode.COUNT_CONSONANTS -> "Все буквы кроме гласных, Ь и Ъ"
            SkillCode.FIND_FIRST_LETTER -> "Посмотри на первую букву слова"
            SkillCode.FIND_LAST_LETTER -> "Посмотри на последнюю букву слова"
            SkillCode.COUNT_LETTERS -> "Посчитай все буквы в слове"
            SkillCode.RECOGNIZE_SOFT_SIGN -> "Мягкий знак выглядит так: Ь"
            SkillCode.RECOGNIZE_HARD_SIGN -> "Твёрдый знак выглядит так: Ъ"
            SkillCode.SPELLING_ZHI_SHI -> "ЖИ-ШИ пиши с буквой И"
            SkillCode.SPELLING_CHA_SCHA -> "ЧА-ЩА пиши с буквой А"
            SkillCode.SPELLING_CHU_SCHU -> "ЧУ-ЩУ пиши с буквой У"
            SkillCode.FIND_ROOT -> "Корень — общая часть родственных слов"
            SkillCode.FIND_PREFIX -> "Приставка стоит перед корнем"
            SkillCode.FIND_SUFFIX -> "Суффикс стоит после корня"
            SkillCode.FIND_ENDING -> "Окончание изменяется: МАМА, МАМЫ, МАМЕ"
        }
    }

    override fun buildCorrectAnswer(request: ExerciseRequest): CorrectAnswer {
        val analysis = request.analysis
        val letter = analysis.letterAnalysis
        val syllable = analysis.syllableAnalysis

        return when (request.skillCode) {
            SkillCode.COUNT_SYLLABLES -> {
                val count = syllable?.count ?: 0
                TextAnswer(count.toString())
            }
            SkillCode.FIND_FIRST_LETTER -> {
                TextAnswer(letter.first.uppercase())
            }
            SkillCode.FIND_LAST_LETTER -> {
                TextAnswer(letter.last.uppercase())
            }
            SkillCode.COUNT_LETTERS -> {
                TextAnswer(letter.count.toString())
            }
            SkillCode.RECOGNIZE_SOFT_SIGN -> {
                TextAnswer(if (letter.hasSoftSign) "Да" else "Нет")
            }
            SkillCode.RECOGNIZE_HARD_SIGN -> {
                TextAnswer(if (letter.hasHardSign) "Да" else "Нет")
            }
            SkillCode.COUNT_VOWELS -> {
                val count = letter.letters.count { it.isVowel }
                TextAnswer(count.toString())
            }
            SkillCode.COUNT_CONSONANTS -> {
                val count = letter.letters.count { it.isConsonant }
                TextAnswer(count.toString())
            }
            SkillCode.DIVIDE_TO_SYLLABLES -> {
                val text = syllable?.syllables?.joinToString("-") { it.text } ?: "?"
                TextAnswer(text)
            }
            SkillCode.FIND_STRESSED_SYLLABLE -> {
                val text = syllable?.syllables?.find { it.isStressed }?.text ?: "?"
                TextAnswer(text)
            }
            else -> TextAnswer("?")
        }
    }

    private fun generateDistractorsFor(
        correct: CorrectAnswer,
        skillCode: SkillCode
    ): List<CorrectAnswer> {
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
                    TextAnswer((num - 1).coerceAtLeast(1).toString()),
                    TextAnswer((num + 1).toString()),
                    TextAnswer((num + 2).toString())
                )
            }
            SkillCode.RECOGNIZE_SOFT_SIGN,
            SkillCode.RECOGNIZE_HARD_SIGN -> {
                if (value == "Да") listOf(TextAnswer("Нет"))
                else listOf(TextAnswer("Да"))
            }
            SkillCode.FIND_FIRST_LETTER,
            SkillCode.FIND_LAST_LETTER -> {
                val chars = listOf("А", "О", "У", "И", "Е")
                    .filter { it != value }
                    .take(3)
                chars.map { TextAnswer(it) }
            }
            else -> emptyList()
        }
    }
}
