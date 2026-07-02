package com.example.russianpath.core.exercise

sealed interface ExerciseOption

data class TextOption(
    val id: String,
    val text: String
) : ExerciseOption
