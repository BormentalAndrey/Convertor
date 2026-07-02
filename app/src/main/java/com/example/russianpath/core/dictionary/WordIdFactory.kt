// app/src/main/java/com/example/russianpath/core/dictionary/WordIdFactory.kt
package com.example.russianpath.core.dictionary

/**
 * Фабрика [WordId].
 * Единственный способ создания WordId во всём проекте.
 *
 * ## Контракт нормализации
 * [normalized] должен быть уже нормализован согласно правилам [WordId]:
 * - Нижний регистр
 * - Форма NFC
 * - Пробелы заменены на `_`
 * - Буква `ё` сохранена
 */
object WordIdFactory {

    fun fromNormalized(normalized: String): WordId {
        require(normalized == normalized.lowercase()) {
            "normalized must already be lowercase: $normalized"
        }
        return WordId("word_$normalized")
    }
}
