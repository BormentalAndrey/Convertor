package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1, // Всегда 1, так как профиль пользователя один
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val gemsBalance: Int = 0,
    val livesCount: Int = 5,
    val totalLessonsCompleted: Int = 0 // <-- Это поле обязательно должно быть здесь
)
