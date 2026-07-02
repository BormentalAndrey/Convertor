// core/dictionary/WordIdFactory.kt
package com.example.russianpath.core.dictionary

/**
 * Фабрика WordId.
 * Единственный способ создания WordId во всём проекте.
 */
object WordIdFactory {

    fun fromNormalized(normalized: String): WordId =
        WordId("word_$normalized")
}
