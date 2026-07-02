package com.example.russianpath.core.dictionary

object WordIdFactory {
    fun fromNormalized(normalized: String): WordId {
        return WordId("word_${normalized.lowercase()}")
    }
}
