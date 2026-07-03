package com.example.russianpath.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * TypeConverter для Room.
 *
 * Преобразует:
 * - List<String> ↔ JSON-строка
 * - Map<String, Any> ↔ JSON-строка
 *
 * Используется для хранения:
 * - Списков пререквизитов
 * - Списков опций вопросов
 * - Списков синонимов/антонимов
 * - Морфемной структуры слова
 * - И других JSON-полей
 */
class StringListConverter {

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    // ========================================================================
    // List<String> ↔ String
    // ========================================================================

    /**
     * Преобразует список строк в JSON-строку.
     * Пустой список → "[]".
     */
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    /**
     * Преобразует JSON-строку в список строк.
     * При ошибке парсинга возвращает пустой список.
     */
    @TypeConverter
    fun toStringList(json: String): List<String> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ========================================================================
    // List<Int> ↔ String
    // ========================================================================

    /**
     * Преобразует список чисел в JSON-строку.
     * Используется для correctOrder в вопросах drag & drop.
     */
    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return gson.toJson(list)
    }

    /**
     * Преобразует JSON-строку в список чисел.
     */
    @TypeConverter
    fun toIntList(json: String): List<Int> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ========================================================================
    // Map<String, Any> ↔ String
    // ========================================================================

    /**
     * Преобразует Map в JSON-строку.
     * Используется для хранения морфемной структуры, пар matching и т.д.
     */
    @TypeConverter
    fun fromStringMap(map: Map<String, Any>): String {
        return gson.toJson(map)
    }

    /**
     * Преобразует JSON-строку в Map.
     */
    @TypeConverter
    fun toStringMap(json: String): Map<String, Any> {
        if (json.isBlank() || json == "{}") return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    // ========================================================================
    // List<Map<String, Any>> ↔ String
    // ========================================================================

    /**
     * Преобразует список Map в JSON-строку.
     * Используется для mistakesJson в LessonCompletionEntity.
     */
    @TypeConverter
    fun fromMapList(list: List<Map<String, Any>>): String {
        return gson.toJson(list)
    }

    /**
     * Преобразует JSON-строку в список Map.
     */
    @TypeConverter
    fun toMapList(json: String): List<Map<String, Any>> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
