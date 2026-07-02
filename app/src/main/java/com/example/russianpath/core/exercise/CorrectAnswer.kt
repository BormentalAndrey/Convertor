package com.example.russianpath.core.exercise

sealed interface CorrectAnswer

data class TextAnswer(val value: String) : CorrectAnswer
data class ChoiceAnswer(val index: Int, val value: String) : CorrectAnswer
