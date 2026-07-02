package com.example.russianpath.core.analysis

import com.example.russianpath.core.dictionary.DictionaryWord

data class WordAnalysis(
    val dictionaryWord: DictionaryWord,
    val letterAnalysis: LetterAnalysis,
    val analyses: Analyses = Analyses()
)
