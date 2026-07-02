package com.example.russianpath.data.exercise

import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.exercise.AnswerProvider
import com.example.russianpath.core.exercise.CorrectAnswer
import com.example.russianpath.core.exercise.TextAnswer
import com.example.russianpath.core.knowledge.SkillCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnswerProviderImpl @Inject constructor() : AnswerProvider {

    override fun getCorrectAnswer(skillCode: SkillCode, analysis: WordAnalysis): CorrectAnswer {
        val letter = analysis.letterAnalysis
        val syllable = analysis.analyses.syllable

        return when (skillCode) {
            SkillCode.COUNT_SYLLABLES -> {
                TextAnswer((syllable?.count ?: 0).toString())
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
                TextAnswer(letter.letters.count { it.isVowel }.toString())
            }
            SkillCode.COUNT_CONSONANTS -> {
                TextAnswer(letter.letters.count { it.isConsonant }.toString())
            }
            SkillCode.DIVIDE_TO_SYLLABLES -> {
                TextAnswer(syllable?.syllables?.joinToString("-") { it.text } ?: "?")
            }
            SkillCode.FIND_STRESSED_SYLLABLE -> {
                TextAnswer(syllable?.syllables?.find { it.isStressed }?.text ?: "?")
            }
            else -> TextAnswer("?")
        }
    }
}
