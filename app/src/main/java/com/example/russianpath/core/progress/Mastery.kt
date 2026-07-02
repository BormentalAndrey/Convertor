package com.example.russianpath.core.progress

import com.example.russianpath.core.knowledge.SkillCode
import java.time.Instant

data class Mastery(
    val microSkillId: String,
    val skillCode: SkillCode,
    val level: Float,
    val confidence: Float,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val lastReviewed: Instant
)
