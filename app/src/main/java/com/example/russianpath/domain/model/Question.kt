package com.example.russianpath.domain.model

data class Question(
    val id: String,
    val lessonId: String,
    val questionType: QuestionType,
    val promptText: String,
    val options: List<String> = emptyList(),
    val draggableWords: List<String> = emptyList(),
    val correctAnswer: String,
    val correctOrder: List<Int> = emptyList(),
    val hintText: String?,
    val audioPath: String?,
    val ruleReference: String? = null
)
