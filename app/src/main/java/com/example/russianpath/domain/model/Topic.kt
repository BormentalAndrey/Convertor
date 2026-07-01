package com.example.russianpath.domain.model

data class Topic(
    val id: String,
    val gradeLevel: Int,
    val title: String,
    val description: String,
    val iconName: String,
    val sortOrder: Int,
    val isUnlocked: Boolean,
    val completionPercentage: Float = 0f,
    val stars: Int = 0
)
