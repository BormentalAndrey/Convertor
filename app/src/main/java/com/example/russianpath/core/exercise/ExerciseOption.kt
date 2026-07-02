package com.example.russianpath.core.exercise

sealed interface ExerciseOption

data class TextOption(
    val id: OptionId,
    val text: String
) : ExerciseOption
