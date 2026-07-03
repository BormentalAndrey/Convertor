package com.example.russianpath.data.seed

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SeedLoader(
    private val context: Context
) {

    private val gson = Gson()

    inline fun <reified T> loadList(fileName: String): List<T> {
        return try {
            val json = context.assets
                .open("seed/$fileName")
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(json, type)

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadString(fileName: String): String {
        return context.assets
            .open("seed/$fileName")
            .bufferedReader()
            .use { it.readText() }
    }
}
