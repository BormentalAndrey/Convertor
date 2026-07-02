package com.example.russianpath.core.progress

import com.example.russianpath.core.knowledge.MicroSkillId
import com.example.russianpath.core.knowledge.SkillCode
import java.time.Instant

data class Mastery(
    val microSkillId: MicroSkillId,
    val skillCode: SkillCode,
    val level: Float,
    val confidence: Float,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val lastReviewed: Instant
) {
    init {
        require(level in 0f..1f) {
            "level must be in 0..1, got $level"
        }
        require(confidence in 0f..1f) {
            "confidence must be in 0..1, got $confidence"
        }
        require(totalAttempts >= 0) {
            "totalAttempts must be >= 0, got $totalAttempts"
        }
        require(correctAttempts >= 0) {
            "correctAttempts must be >= 0, got $correctAttempts"
        }
        require(correctAttempts <= totalAttempts) {
            "correctAttempts ($correctAttempts) must be <= totalAttempts ($totalAttempts)"
        }
    }
}
