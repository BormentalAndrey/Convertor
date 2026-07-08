// app/src/main/java/com/example/russianpath/data/seed/SeedLoader.kt

package com.example.russianpath.data.seed

import android.content.Context
import android.util.Log
import com.example.russianpath.data.seed.model.ContentFile
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Загрузчик seed-файлов из assets.
 *
 * Читает JSON-файлы из assets/seed/ и десериализует в список указанного типа.
 * Поддерживает:
 * - Загрузку списков сущностей
 * - Загрузку произвольных строк
 * - Проверку существования файла
 *
 * Gson настроен с FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
 * для автоматического преобразования snake_case (JSON) ↔ camelCase (Kotlin).
 */
@Singleton
class SeedLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @PublishedApi
    internal val gson: Gson = GsonBuilder()
        .setLenient()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    /**
     * Загружает список сущностей из JSON-файла.
     *
     * @param fileName Имя файла относительно assets/seed/ (например, "grades.json")
     * @return Список десериализованных объектов. Пустой список при ошибке.
     */
    inline fun <reified T> loadList(fileName: String): List<T> {
        return try {
            val json = readFile(fileName)
            if (json.isBlank()) {
                Log.w("SeedLoader", "Файл $fileName пуст")
                emptyList()
            } else {
                val type = object : TypeToken<List<T>>() {}.type
                val list: List<T> = gson.fromJson(json, type) ?: emptyList()
                Log.d("SeedLoader", "Загружен $fileName: ${list.size} записей")
                list
            }
        } catch (e: ContentLoadException) {
            Log.e("SeedLoader", "Файл не найден: $fileName - ${e.message}")
            emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e("SeedLoader", "Ошибка парсинга JSON: $fileName - ${e.message}")
            emptyList()
        } catch (e: Exception) {
            Log.e("SeedLoader", "Ошибка загрузки $fileName: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Загружает список сущностей с расширенной обработкой ошибок.
     * Возвращает Result для функциональной обработки.
     *
     * @param fileName Имя файла.
     * @return Result с данными или исключением.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> loadListSafe(fileName: String): Result<List<T>> {
        return try {
            val json = readFile(fileName)
            if (json.isBlank()) {
                Result.success(emptyList())
            } else {
                val type = object : TypeToken<List<T>>() {}.type
                val rawList: Any? = gson.fromJson(json, type)
                val list: List<T> = (rawList as? List<*>)?.filterIsInstance<T>() ?: emptyList()
                Result.success(list)
            }
        } catch (e: IOException) {
            Result.failure(ContentLoadException("File not found: seed/$fileName", e))
        } catch (e: JsonSyntaxException) {
            Result.failure(ContentLoadException("Invalid JSON in: seed/$fileName", e))
        } catch (e: Exception) {
            Result.failure(ContentLoadException("Failed to load: seed/$fileName", e))
        }
    }

    /**
     * Загружает содержимое файла как строку.
     *
     * @param fileName Имя файла относительно assets/seed/.
     * @return Содержимое файла.
     * @throws ContentLoadException если файл не найден.
     */
    fun loadString(fileName: String): String {
        return readFile(fileName)
    }

    /**
     * Безопасная загрузка строки с возвратом null при ошибке.
     */
    fun loadStringOrNull(fileName: String): String? {
        return try {
            readFile(fileName)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Загружает контент из ContentFile модели манифеста.
     *
     * @param contentFile Модель файла из манифеста.
     * @return Содержимое файла как строка.
     */
    fun loadContentFile(contentFile: ContentFile): String {
        return readFile(contentFile.path)
    }

    /**
     * Проверяет существование файла в assets/seed/.
     */
    fun fileExists(fileName: String): Boolean {
        return try {
            val path = if (fileName.startsWith("seed/")) fileName else "seed/$fileName"
            context.assets.open(path).close()
            true
        } catch (_: IOException) {
            false
        }
    }

    /**
     * Возвращает список доступных seed-файлов.
     */
    fun listSeedFiles(): List<String> {
        return try {
            val files = context.assets.list("seed")?.toList() ?: emptyList()
            Log.d("SeedLoader", "Файлы в seed/: ${files.joinToString(", ")}")
            files
        } catch (e: IOException) {
            Log.e("SeedLoader", "Ошибка чтения списка файлов: ${e.message}")
            emptyList()
        }
    }

    /**
     * Читает содержимое файла из assets/seed/.
     *
     * @param fileName Имя файла (может быть с префиксом seed/ или без).
     * @return Содержимое файла.
     * @throws ContentLoadException если файл не найден.
     */
    @PublishedApi
    internal fun readFile(fileName: String): String {
        val path = if (fileName.startsWith("seed/")) fileName else "seed/$fileName"

        return try {
            val content = context.assets
                .open(path)
                .bufferedReader()
                .use { it.readText() }
            
            Log.d("SeedLoader", "Прочитан файл $path (${content.length} байт)")
            content
        } catch (e: IOException) {
            Log.e("SeedLoader", "Не удалось прочитать $path: ${e.message}")
            throw ContentLoadException(
                "Cannot read file: $path. Ensure the file is placed in assets/seed/ " +
                        "and is not corrupted. Error: ${e.message}",
                e
            )
        }
    }
}
