package com.example.russianpath.core.exercise

interface TemplateEngine {
    fun buildPrompt(request: ExerciseRequest): String
    fun buildOptions(request: ExerciseRequest): List<ExerciseOption>
    fun buildHint(request: ExerciseRequest): String
    fun buildCorrectAnswer(request: ExerciseRequest): CorrectAnswer
}
