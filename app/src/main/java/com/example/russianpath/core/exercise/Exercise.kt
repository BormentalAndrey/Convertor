package com.example.russianpath.core.exercise

data class Exercise(
    val id: ExerciseId,
    val fingerprint: ExerciseFingerprint,
    val exerciseType: ExerciseType,
    val presentationType: PresentationType,
    val prompt: String,
    val options: List<ExerciseOption>,
    val correctAnswer: CorrectAnswer,
    val hint: String?
)
