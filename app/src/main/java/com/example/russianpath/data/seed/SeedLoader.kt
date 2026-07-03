package com.example.russianpath.data.seed

import android.content.Context
import com.example.russianpath.data.seed.model.ContentFile
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
 * - Потоковую загрузку больших файлов (для будущего использования)
 */
@Singleton
class SeedLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val gson: Gson = GsonBuilder()
        .setLenient()
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
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
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
    inline fun <reified T> loadListSafe(fileName: String): Result<List<T>> {
        return try {
            val json = readFile(fileName)
            val type = object : TypeToken<List<T>>() {}.type
            val list: List<T> = gson.fromJson(json, type) ?: emptyList()
            Result.success(list)
        } catch (e: IOException) {
            Result.failure(
                ContentLoadException("File not found: seed/$fileName", e)
            )
        } catch (e: JsonSyntaxException) {
            Result.failure(
                ContentLoadException("Invalid JSON in: seed/$fileName", e)
            )
        } catch (e: Exception) {
            Result.failure(
                ContentLoadException("Failed to load: seed/$fileName", e)
            )
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
            context.assets.open("seed/$fileName").close()
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
            context.assets.list("seed")?.toList() ?: emptyList()
        } catch (_: IOException) {
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
    private fun readFile(fileName: String): String {
        val path = if (fileName.startsWith("seed/")) fileName else "seed/$fileName"

        return try {
            context.assets
                .open(path)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            throw ContentLoadException(
                "Cannot read file: $path. Ensure the file is placed in assets/seed/ " +
                        "and is not corrupted.",
                e
            )
        }
    }
}
