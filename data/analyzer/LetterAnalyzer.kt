package com.example.russianpath.data.analyzer

import com.example.russianpath.core.analysis.Letter
import com.example.russianpath.core.analysis.LetterAnalysis
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LetterAnalyzer @Inject constructor(
    private val vowelDetector: VowelDetector
) {

    fun analyze(word: String): LetterAnalysis {
        val letters = word.mapIndexed { index, char ->
            Letter(
                char = char,
                position = index,
                isVowel = vowelDetector.isVowel(char),
                isConsonant = vowelDetector.isConsonant(char),
                isSign = char in setOf('Ь', 'Ъ')
            )
        }

        return LetterAnalysis(
            letters = letters,
            count = letters.size,
            first = letters.first().char,
            last = letters.last().char,
            hasSoftSign = letters.any { it.isSign && it.char == 'Ь' },
            hasHardSign = letters.any { it.isSign && it.char == 'Ъ' }
        )
    }
}
