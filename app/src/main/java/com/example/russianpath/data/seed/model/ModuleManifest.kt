package com.example.russianpath.data.seed.model

import com.google.gson.annotations.SerializedName

/**
 * Модель модуля контента в манифесте.
 *
 * Каждый модуль соответствует одному типу данных (grades, topics, lessons и т.д.)
 * и ссылается на JSON-файл в assets/seed/.
 *
 * Пример:
 * {
 *   "id": "topics",
 *   "type": "data",
 *   "name": "Темы 5-11 класс",
 *   "archive": "topics.json",
 *   "enabled": true,
 *   "priority": 10
 * }
 */
data class ModuleManifest(
    @SerializedName("id")
    val id: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("archive")
    val archive: String,

    @SerializedName("enabled")
    val enabled: Boolean = true,

    @SerializedName("priority")
    val priority: Int = 0
) {

    /**
     * Формирует полный путь к файлу модуля в assets.
     */
    fun getAssetPath(): String {
        return "seed/$archive"
    }

    /**
     * Проверяет, является ли модуль словарём.
     */
    fun isDictionary(): Boolean {
        return type == "dictionary"
    }

    /**
     * Проверяет, является ли модуль правилом.
     */
    fun isRuleBook(): Boolean {
        return type == "rulebook"
    }

    companion object {
        /** Тип модуля: основные данные (grades, sections, topics и т.д.) */
        const val TYPE_DATA = "data"

        /** Тип модуля: словарь */
        const val TYPE_DICTIONARY = "dictionary"

        /** Тип модуля: свод правил */
        const val TYPE_RULEBOOK = "rulebook"

        /** Тип модуля: медиа-контент (аудио, изображения) */
        const val TYPE_MEDIA = "media"
    }
}
