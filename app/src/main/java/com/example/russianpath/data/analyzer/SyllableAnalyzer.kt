package com.example.russianpath.data.analyzer

import com.example.russianpath.core.analysis.Syllable
import com.example.russianpath.core.analysis.SyllableAnalysis
import com.example.russianpath.core.dictionary.DictionaryWord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyllableAnalyzer @Inject constructor(
    private val vowelDetector: VowelDetector,
    private val syllableSplitter: SyllableSplitter
) {

    fun analyze(word: DictionaryWord): SyllableAnalysis {
        val rawSyllables = word.syllables
            ?: syllableSplitter.split(word.word)
        val syllables = rawSyllables.mapIndexed { index, text ->
            Syllable(
                text = text,
                position = index,
                isStressed = index == word.stressPosition
            )
        }

        return SyllableAnalysis(
            syllables = syllables,
            count = syllables.size
        )
    }
}
