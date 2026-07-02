package com.example.russianpath.core.exercise

interface TemplateEngine {
    fun buildPrompt(request: ExerciseRequest): String
    fun buildHint(request: ExerciseRequest): String
}
