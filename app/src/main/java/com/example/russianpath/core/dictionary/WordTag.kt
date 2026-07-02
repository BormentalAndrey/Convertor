package com.example.russianpath.core.dictionary

/**
 * Неаналитические словарные признаки слова.
 * Не вычисляются RussianAnalyzer'ом.
 */
enum class WordTag {
    /** Имя собственное (Маша, Москва) */
    PROPER_NAME,
    /** Частотное слово первого класса */
    FREQUENT
}
