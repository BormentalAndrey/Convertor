// app/src/main/java/com/example/russianpath/core/dictionary/WordIdFactory.kt
package com.example.russianpath.core.dictionary

/**
 * Фабрика [WordId].
 * Единственный способ создания WordId во всём проекте.
 *
 * ## Контракт
 * Фабрика **не выполняет нормализацию**. Она принимает уже нормализованную строку
 * и проверяет базовое предусловие (lowercase).
 *
 * Полная нормализация (NFC, пробелы → `_`, сохранение `ё`)
 * выполняется до вызова фабрики — на уровне парсера словаря
 * или импорта данных.
 *
 * @throws IllegalArgumentException если [normalized] не в нижнем регистре.
 */
object WordIdFactory {

    fun fromNormalized(normalized: String): WordId {
        require(normalized == normalized.lowercase()) {
            "normalized must already be lowercase: $normalized"
        }
        return WordId("word_$normalized")
    }
}
