package com.example.russianpath.data.local.converter

import androidx.room.TypeConverter
import com.example.russianpath.core.dictionary.WordTag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WordTagListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromWordTags(tags: Set<WordTag>): String =
        gson.toJson(tags.map { it.name })

    @TypeConverter
    fun toWordTags(json: String): Set<WordTag> {
        if (json.isBlank()) return emptySet()
        val type = object : TypeToken<List<String>>() {}.type
        val names: List<String> = gson.fromJson(json, type)
        return names.map { WordTag.valueOf(it) }.toSet()
    }
}
