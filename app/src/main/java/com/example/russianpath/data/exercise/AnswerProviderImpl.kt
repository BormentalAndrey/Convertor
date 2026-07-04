// app/src/main/java/com/example/russianpath/data/exercise/AnswerProviderImpl.kt

package com.example.russianpath.data.exercise

import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.exercise.AnswerProvider
import com.example.russianpath.core.exercise.CorrectAnswer
import com.example.russianpath.core.exercise.TextAnswer
import com.example.russianpath.data.local.converter.SkillCode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnswerProviderImpl @Inject constructor() : AnswerProvider {

    override fun getCorrectAnswer(skillCode: SkillCode, analysis: WordAnalysis): CorrectAnswer {
        val letter = analysis.letterAnalysis
        val syllable = analysis.analyses.syllable

        return when (skillCode) {
            SkillCode.PHONETIC_ANALYSIS -> {
                TextAnswer((syllable?.count ?: 0).toString())
            }
            SkillCode.IDENTIFY_LANGUAGE_UNITS -> {
                TextAnswer(letter.first.uppercase())
            }
            SkillCode.MORPHEMIC_ANALYSIS -> {
                TextAnswer(letter.last.uppercase())
            }
            SkillCode.ORTHOGRAPHIC_ANALYSIS -> {
                TextAnswer(letter.count.toString())
            }
            SkillCode.LEXICAL_MEANS -> {
                TextAnswer(if (letter.hasSoftSign) "Да" else "Нет")
            }
            SkillCode.SPEECH_STYLES -> {
                TextAnswer(if (letter.hasHardSign) "Да" else "Нет")
            }
            SkillCode.MORPHOLOGICAL_ANALYSIS -> {
                TextAnswer(letter.letters.count { it.isVowel }.toString())
            }
            SkillCode.SYNTACTIC_ANALYSIS -> {
                TextAnswer(letter.letters.count { it.isConsonant }.toString())
            }
            SkillCode.PUNCTUATION_ANALYSIS -> {
                TextAnswer(syllable?.syllables?.joinToString("-") { it.text } ?: "?")
            }
            SkillCode.ORTHOGRAPHY_NORMS -> {
                TextAnswer(syllable?.syllables?.find { it.isStressed }?.text ?: "?")
            }
            else -> TextAnswer("?")
        }
    }
}
