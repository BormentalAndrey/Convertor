package com.example.russianpath

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RussianPathApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализация при первом запуске (распаковка БД)
        DatabaseInitializer.initialize(this)
    }
}
