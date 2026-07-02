package com.example.russianpath.core.progress

import com.example.russianpath.core.exercise.Exercise

interface AnswerEvaluator {
    fun evaluate(exercise: Exercise, answer: UserAnswer): AnswerResult
}
