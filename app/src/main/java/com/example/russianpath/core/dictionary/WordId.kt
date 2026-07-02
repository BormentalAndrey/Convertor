package com.example.russianpath.core.dictionary

/**
 * Идентификатор слова в словаре.
 *
 * ## Формат
 * `word_` + `normalized`
 *
 * ## Правила нормализации (часть контракта v1.0.0):
 * - Приведение к нижнему регистру: `Мама` → `мама`
 * - Буква `ё` сохраняется: `ёлка` → `ёлка`
 * - Дефисы сохраняются: `кто-то` → `кто-то`
 * - Пробелы заменяются на `_`: `день рождения` → `день_рождения`
 * - Форма NFC (Canonical Composition)
 *
 * ## Примеры
 * ```
 * WordId("word_мама")
 * WordId("word_ёлка")
 * WordId("word_кто-то")
 * WordId("word_день_рождения")
 * ```
 */
@JvmInline
value class WordId(val value: String)
