package com.example.russianpath.data.analyzer

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Упрощённый алгоритм деления на слоги.
 *
 * Правило: слог заканчивается на гласную.
 * Для первой вертикали покрывает базовые случаи.
 */
@Singleton
class SyllableSplitter @Inject constructor(
    private val vowelDetector: VowelDetector
) {

    fun split(word: String): List<String> {
        val result = mutableListOf<String>()
        var currentSyllable = StringBuilder()

        for (char in word) {
            currentSyllable.append(char)
            if (vowelDetector.isVowel(char)) {
                result.add(currentSyllable.toString())
                currentSyllable = StringBuilder()
            }
        }

        if (currentSyllable.isNotEmpty()) {
            // Остаток присоединяем к последнему слогу
            if (result.isNotEmpty()) {
                result[result.lastIndex] = result.last() + currentSyllable.toString()
            } else {
                result.add(currentSyllable.toString())
            }
        }

        return result
    }
}
