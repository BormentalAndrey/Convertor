package com.example.russianpath.data.seed.model

import com.google.gson.annotations.SerializedName

/**
 * Корневая модель манифеста контента.
 *
 * Описывает версию схемы, версию контента и список модулей для загрузки.
 * Используется ManifestLoader при старте приложения для определения необходимости сидирования.
 *
 * Пример JSON-структуры:
 * {
 *   "schemaVersion": 2,
 *   "contentVersion": 5,
 *   "generated": "2026-07-01T12:00:00Z",
 *   "modules": [...],
 *   "dictionaries": [...],
 *   "ruleBooks": [...]
 * }
 */
data class SeedManifest(
    @SerializedName("schemaVersion")
    val schemaVersion: Int,

    @SerializedName("contentVersion")
    val contentVersion: Int,

    @SerializedName("generated")
    val generated: String,

    @SerializedName("modules")
    val modules: List<ModuleManifest> = emptyList(),

    @SerializedName("dictionaries")
    val dictionaries: List<ContentFile> = emptyList(),

    @SerializedName("ruleBooks")
    val ruleBooks: List<ContentFile> = emptyList()
) {

    /**
     * Проверяет, требуется ли полное пересидирование на основе версии схемы.
     * Если сохранённая версия схемы меньше текущей — нужна миграция контента.
     */
    fun requiresSchemaMigration(savedSchemaVersion: Int): Boolean {
        return schemaVersion > savedSchemaVersion
    }

    /**
     * Проверяет, требуется ли обновление контента на основе версии контента.
     * Если сохранённая версия контента меньше текущей — нужна инкрементальная загрузка.
     */
    fun requiresContentUpdate(savedContentVersion: Int): Boolean {
        return contentVersion > savedContentVersion
    }

    /**
     * Возвращает список включённых модулей, отсортированных по приоритету.
     */
    fun getEnabledModules(): List<ModuleManifest> {
        return modules
            .filter { it.enabled }
            .sortedByDescending { it.priority }
    }

    /**
     * Находит модуль по идентификатору.
     */
    fun findModuleById(id: String): ModuleManifest? {
        return modules.firstOrNull { it.id == id }
    }
}
