package com.example.russianpath.core.exercise

data class Exercise(
    val id: String,
    val prompt: String,
    val options: List<ExerciseOption>,
    val correctAnswer: CorrectAnswer,
    val hint: String?,
    val metadata: ExerciseMetadata
)
