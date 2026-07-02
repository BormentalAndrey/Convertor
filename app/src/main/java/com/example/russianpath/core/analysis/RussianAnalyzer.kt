package com.example.russianpath.core.analysis

import com.example.russianpath.core.dictionary.DictionaryWord

interface RussianAnalyzer {
    fun analyze(word: DictionaryWord): WordAnalysis
}
