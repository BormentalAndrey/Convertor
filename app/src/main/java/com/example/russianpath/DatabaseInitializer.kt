package com.example.russianpath

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object DatabaseInitializer {
    private const val DB_NAME = "russian_path.db"

    fun initialize(context: Context) {
        val dbFile = context.getDatabasePath(DB_NAME)

        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()
            try {
                context.assets.open("databases/$DB_NAME").use { input ->
                    FileOutputStream(dbFile).use { output ->
                        input.copyTo(output)
                    }
                }
                Log.d("DB_INIT", "Database v2 copied successfully")
            } catch (e: Exception) {
                Log.e("DB_INIT", "Error copying database, will create empty", e)
            }
        }
    }
}
