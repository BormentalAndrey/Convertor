package com.example.russianpath.core.knowledge

data class LearningObjective(
    val id: ObjectiveId,
    val name: String,
    val description: String,
    val prerequisites: List<ObjectiveId> = emptyList()
)
