// app/src/main/java/com/example/russianpath/data/analyzer/LetterAnalyzer.kt
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
        require(word.isNotBlank()) {
            "Word must not be blank"
        }

        val letters = word.mapIndexed { index, char ->
            Letter(
                char = char,
                position = index,
                isVowel = vowelDetector.isVowel(char),
                isConsonant = vowelDetector.isConsonant(char),
                isSign = vowelDetector.isSign(char)
            )
        }

        return LetterAnalysis(
            letters = letters,
            count = letters.size,
            first = letters.first().char,
            last = letters.last().char,
            hasSoftSign = letters.any { it.char.uppercaseChar() == 'Ь' },
            hasHardSign = letters.any { it.char.uppercaseChar() == 'Ъ' }
        )
    }
}
