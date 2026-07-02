package com.example.russianpath.core.progress

import java.time.Instant

data class AnswerResult(
    val exerciseId: String,
    val userAnswer: UserAnswer,
    val isCorrect: Boolean,
    val timeSpentMs: Long,
    val timestamp: Instant
)
