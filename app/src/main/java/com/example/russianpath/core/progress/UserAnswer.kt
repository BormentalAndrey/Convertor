package com.example.russianpath.core.progress

sealed interface UserAnswer

data class TextUserAnswer(val text: String) : UserAnswer
data class ChoiceUserAnswer(val index: Int) : UserAnswer
