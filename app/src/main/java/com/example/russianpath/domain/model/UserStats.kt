package com.example.russianpath.domain.model

data class UserStats(
    val totalXp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val gemsBalance: Int = 50,
    val livesCount: Int = 5,
    val totalLessonsCompleted: Int = 0,
    val accuracy: Float = 100f
)
