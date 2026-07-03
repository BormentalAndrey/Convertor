package com.example.russianpath.data.seed

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер версий контента.
 *
 * Хранит в SharedPreferences:
 * - schemaVersion — версия схемы БД контента (полное пересидирование)
 * - contentVersion — версия контента (инкрементальное обновление)
 *
 * Используется для определения необходимости обновления контента при старте приложения.
 */
@Singleton
class ContentVersionManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Возвращает сохранённую версию схемы.
     * 0 — контент никогда не загружался.
     */
    fun getSchemaVersion(): Int {
        return prefs.getInt(KEY_SCHEMA, 0)
    }

    /**
     * Возвращает сохранённую версию контента.
     * 0 — контент никогда не загружался.
     */
    fun getContentVersion(): Int {
        return prefs.getInt(KEY_CONTENT, 0)
    }

    /**
     * Сохраняет версию схемы.
     */
    fun saveSchemaVersion(version: Int) {
        prefs.edit { putInt(KEY_SCHEMA, version) }
    }

    /**
     * Сохраняет версию контента.
     */
    fun saveContentVersion(version: Int) {
        prefs.edit { putInt(KEY_CONTENT, version) }
    }

    /**
     * Атомарно обновляет обе версии.
     * Вызывается после успешного завершения сидирования.
     */
    fun update(schemaVersion: Int, contentVersion: Int) {
        prefs.edit {
            putInt(KEY_SCHEMA, schemaVersion)
            putInt(KEY_CONTENT, contentVersion)
        }
    }

    /**
     * Сохраняет версию конкретного модуля.
     * Ключ формируется как "module_{moduleId}_version".
     */
    fun saveModuleVersion(moduleId: String, version: Int) {
        prefs.edit { putInt("module_${moduleId}_version", version) }
    }

    /**
     * Возвращает сохранённую версию конкретного модуля.
     */
    fun getModuleVersion(moduleId: String): Int {
        return prefs.getInt("module_${moduleId}_version", 0)
    }

    /**
     * Сохраняет контрольную сумму файла.
     * Используется для проверки целостности и инкрементального обновления.
     */
    fun saveFileChecksum(filePath: String, checksum: String) {
        prefs.edit { putString("checksum_$filePath", checksum) }
    }

    /**
     * Возвращает сохранённую контрольную сумму файла.
     */
    fun getFileChecksum(filePath: String): String {
        return prefs.getString("checksum_$filePath", "") ?: ""
    }

    /**
     * Проверяет, была ли выполнена первичная загрузка контента.
     */
    fun isContentLoaded(): Boolean {
        return getSchemaVersion() > 0
    }

    /**
     * Полный сброс всех версий (для тестирования и принудительного обновления).
     */
    fun clear() {
        prefs.edit { clear() }
    }

    private companion object {
        const val PREFS_NAME = "content_versions"
        const val KEY_SCHEMA = "schema_version"
        const val KEY_CONTENT = "content_version"
    }
}
