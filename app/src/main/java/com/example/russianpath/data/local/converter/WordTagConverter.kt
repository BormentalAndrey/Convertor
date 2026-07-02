package com.example.russianpath.data.local.converter

import androidx.room.TypeConverter
import com.example.russianpath.core.dictionary.WordTag

class WordTagConverter {

    @TypeConverter
    fun fromWordTags(tags: Set<WordTag>): String =
        tags.joinToString(",") { it.name }

    @TypeConverter
    fun toWordTags(value: String): Set<WordTag> =
        if (value.isBlank()) emptySet()
        else value.split(",").map { WordTag.valueOf(it.trim()) }.toSet()
}
