package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey
    val id: String,
    val topicId: String,
    val lessonType: String,
    val difficulty: Int,
    val theoryJson: String?,
    val sortOrder: Int,
    val title: String
)
