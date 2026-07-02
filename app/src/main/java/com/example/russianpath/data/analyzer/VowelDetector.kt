package com.example.russianpath.data.analyzer

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VowelDetector @Inject constructor() {

    private val vowels = setOf('А', 'О', 'У', 'Ы', 'Э', 'И', 'Е', 'Ё', 'Ю', 'Я')
    private val signs = setOf('Ь', 'Ъ')

    fun isVowel(char: Char): Boolean = char.uppercaseChar() in vowels

    fun isConsonant(char: Char): Boolean =
        char.uppercaseChar() !in vowels && char.uppercaseChar() !in signs
}
