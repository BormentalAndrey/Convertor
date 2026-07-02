package com.example.russianpath.core.progress

import com.example.russianpath.core.exercise.OptionId

sealed interface UserAnswer

data class TextUserAnswer(val text: String) : UserAnswer
data class ChoiceUserAnswer(val optionId: OptionId) : UserAnswer
