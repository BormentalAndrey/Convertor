package com.example.russianpath.core.progress

import com.example.russianpath.core.knowledge.SkillCode
import java.time.Instant

data class AnswerResult(
    val exerciseId: String,
    val skillCode: SkillCode,
    val userAnswer: UserAnswer,
    val isCorrect: Boolean,
    val timeSpentMs: Long,
    val timestamp: Instant
)
