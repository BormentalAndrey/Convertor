package com.example.russianpath.data.seed.model

import com.google.gson.annotations.SerializedName

/**
 * Модель файла контента (словарь, свод правил).
 *
 * Описывает путь к файлу, контрольную сумму, версию и флаг сжатия.
 * Используется для инкрементального обновления: если checksum изменился —
 * файл нужно перезагрузить.
 *
 * Пример:
 * {
 *   "path": "dictionary.json",
 *   "checksum": "a1b2c3d4e5f6...",
 *   "version": 3,
 *   "compressed": false
 * }
 */
data class ContentFile(
    @SerializedName("path")
    val path: String,

    @SerializedName("checksum")
    val checksum: String = "",

    @SerializedName("version")
    val version: Int = 1,

    @SerializedName("compressed")
    val compressed: Boolean = false
) {

    /**
     * Формирует полный путь к файлу в assets.
     */
    fun getAssetPath(): String {
        return "seed/$path"
    }

    /**
     * Проверяет, требуется ли обновление файла.
     * Сравнивает текущую версию с сохранённой.
     */
    fun requiresUpdate(savedVersion: Int): Boolean {
        return version > savedVersion
    }

    /**
     * Проверяет, является ли файл сжатым.
     */
    fun isCompressed(): Boolean = compressed
}
