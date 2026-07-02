package com.example.russianpath.core.knowledge

data class LearningObjective(
    val id: String,
    val name: String,
    val description: String,
    val prerequisites: List<String> = emptyList()
)
