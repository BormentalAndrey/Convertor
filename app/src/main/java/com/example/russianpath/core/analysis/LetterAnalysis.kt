package com.example.russianpath.core.analysis

data class LetterAnalysis(
    val letters: List<Letter>,
    val count: Int,
    val first: Char,
    val last: Char,
    val hasSoftSign: Boolean,
    val hasHardSign: Boolean
) : Analysis
