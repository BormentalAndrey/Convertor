package com.example.russianpath.core.exercise

interface ExerciseBuilder {
    fun build(request: ExerciseRequest): Exercise
}
