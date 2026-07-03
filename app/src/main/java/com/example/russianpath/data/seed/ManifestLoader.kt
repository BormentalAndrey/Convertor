package com.example.russianpath.data.seed

import android.content.Context
import com.example.russianpath.data.seed.model.SeedManifest
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Загрузчик манифеста контента.
 *
 * Читает manifest.json из assets/seed/ и десериализует в SeedManifest.
 * Манифест содержит метаданные о версиях и списке модулей для загрузки.
 */
@Singleton
class ManifestLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    /**
     * Загружает манифест из стандартного пути.
     *
     * @return SeedManifest с метаданными контента.
     * @throws ContentLoadException если файл не найден или повреждён.
     */
    fun load(): SeedManifest {
        return load(SeedConstants.MANIFEST)
    }

    /**
     * Загружает манифест из указанного пути в assets.
     *
     * @param path Путь к файлу манифеста относительно assets.
     * @return SeedManifest.
     * @throws ContentLoadException при ошибках загрузки или парсинга.
     */
    fun load(path: String): SeedManifest {
        val json = try {
            context.assets
                .open(path)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            throw ContentLoadException(
                message = "Cannot open manifest file: $path. " +
                        "Ensure the file exists in assets/seed/ directory.",
                cause = e
            )
        } catch (e: SecurityException) {
            throw ContentLoadException(
                message = "Permission denied reading manifest: $path",
                cause = e
            )
        }

        return try {
            gson.fromJson(json, SeedManifest::class.java)
        } catch (e: JsonSyntaxException) {
            throw ContentLoadException(
                message = "Invalid JSON in manifest file: $path. " +
                        "Check JSON syntax and field types.",
                cause = e
            )
        } catch (e: NullPointerException) {
            throw ContentLoadException(
                message = "Required fields missing in manifest: $path. " +
                        "Ensure schemaVersion, contentVersion and generated are present.",
                cause = e
            )
        }
    }

    /**
     * Безопасная загрузка манифеста с возвратом null при ошибке.
     * Используется, когда отсутствие манифеста — допустимая ситуация
     * (например, первая установка без seed-файлов).
     */
    fun loadOrNull(): SeedManifest? {
        return try {
            load()
        } catch (_: ContentLoadException) {
            null
        }
    }
}

/**
 * Исключение, возникающее при ошибках загрузки контента.
 */
class ContentLoadException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
