package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_completion")
data class LessonCompletionEntity(
    @PrimaryKey
    val lessonId: String,
    val stars: Int = 0,
    val mistakesCount: Int = 0,
    val completedAt: Long = 0,
    val xpEarned: Int = 0
)
