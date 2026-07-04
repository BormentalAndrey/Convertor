// app/src/main/java/com/example/russianpath/data/seed/SeedLoader.kt

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

@Singleton
class SeedLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @PublishedApi
    internal val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

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

    inline fun <reified T> loadListSafe(fileName: String): Result<List<T>> {
        return try {
            val json = readFile(fileName)
            val type = object : TypeToken<List<T>>() {}.type
            val list: List<T> = gson.fromJson(json, type) ?: emptyList()
            Result.success(list)
        } catch (e: IOException) {
            Result.failure(ContentLoadException("File not found: seed/$fileName", e))
        } catch (e: JsonSyntaxException) {
            Result.failure(ContentLoadException("Invalid JSON in: seed/$fileName", e))
        } catch (e: Exception) {
            Result.failure(ContentLoadException("Failed to load: seed/$fileName", e))
        }
    }

    fun loadString(fileName: String): String {
        return readFile(fileName)
    }

    fun loadStringOrNull(fileName: String): String? {
        return try {
            readFile(fileName)
        } catch (_: Exception) {
            null
        }
    }

    fun loadContentFile(contentFile: ContentFile): String {
        return readFile(contentFile.path)
    }

    fun fileExists(fileName: String): Boolean {
        return try {
            context.assets.open("seed/$fileName").close()
            true
        } catch (_: IOException) {
            false
        }
    }

    fun listSeedFiles(): List<String> {
        return try {
            context.assets.list("seed")?.toList() ?: emptyList()
        } catch (_: IOException) {
            emptyList()
        }
    }

    @PublishedApi
    internal fun readFile(fileName: String): String {
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
