package com.example.russianpath.data.analyzer

import com.example.russianpath.core.analysis.Analyses
import com.example.russianpath.core.analysis.RussianAnalyzer
import com.example.russianpath.core.analysis.WordAnalysis
import com.example.russianpath.core.dictionary.DictionaryWord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RussianAnalyzerImpl @Inject constructor(
    private val letterAnalyzer: LetterAnalyzer,
    private val syllableAnalyzer: SyllableAnalyzer
) : RussianAnalyzer {

    override fun analyze(word: DictionaryWord): WordAnalysis {
        val letterAnalysis = letterAnalyzer.analyze(word.word)
        val syllableAnalysis = syllableAnalyzer.analyze(word)

        return WordAnalysis(
            dictionaryWord = word,
            letterAnalysis = letterAnalysis,
            analyses = Analyses(syllable = syllableAnalysis)
        )
    }
}
