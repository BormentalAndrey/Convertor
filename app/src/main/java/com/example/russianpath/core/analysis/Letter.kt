package com.example.russianpath.core.analysis

data class Letter(
    val char: Char,
    val position: Int,
    val isVowel: Boolean,
    val isConsonant: Boolean,
    val isSign: Boolean
)
