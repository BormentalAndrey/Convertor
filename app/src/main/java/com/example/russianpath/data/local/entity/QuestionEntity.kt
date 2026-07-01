package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val questionType: String,
    val promptText: String,
    val dataJson: String,
    val correctAnswerJson: String,
    val hintText: String?,
    val audioPath: String?
)
