package com.example.russianpath.data.repository

import com.example.russianpath.core.knowledge.MicroSkillId
import com.example.russianpath.core.knowledge.SkillCode
import com.example.russianpath.core.progress.AnswerResult
import com.example.russianpath.core.progress.Mastery
import com.example.russianpath.core.repository.ProgressRepository
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor() : ProgressRepository {

    private val masteryStore = mutableMapOf<MicroSkillId, Mastery>()

    override suspend fun recordAnswer(result: AnswerResult) {
        val current = masteryStore[result.skillCode.let {
            MicroSkillId("ms_${it.key}")
        }] ?: Mastery(
            microSkillId = MicroSkillId("ms_${result.skillCode.key}"),
            skillCode = result.skillCode,
            level = 0f,
            confidence = 0f,
            totalAttempts = 0,
            correctAttempts = 0,
            lastReviewed = Instant.now()
        )

        val updated = current.copy(
            totalAttempts = current.totalAttempts + 1,
            correctAttempts = if (result.isCorrect) {
                current.correctAttempts + 1
            } else {
                current.correctAttempts
            },
            level = calculateLevel(current, result.isCorrect),
            confidence = calculateConfidence(current, result.isCorrect),
            lastReviewed = Instant.now()
        )

        masteryStore[updated.microSkillId] = updated
    }

    override suspend fun getMastery(microSkillId: MicroSkillId): Mastery {
        return masteryStore[microSkillId] ?: Mastery(
            microSkillId = microSkillId,
            skillCode = SkillCode.COUNT_SYLLABLES,
            level = 0f,
            confidence = 0f,
            totalAttempts = 0,
            correctAttempts = 0,
            lastReviewed = Instant.now()
        )
    }

    private fun calculateLevel(current: Mastery, isCorrect: Boolean): Float {
        val ratio = current.correctAttempts.toFloat() /
                (current.totalAttempts + 1).coerceAtLeast(1)
        return ratio.coerceIn(0f, 1f)
    }

    private fun calculateConfidence(current: Mastery, isCorrect: Boolean): Float {
        val confidence = if (current.totalAttempts < 3) {
            0.5f
        } else {
            current.correctAttempts.toFloat() / current.totalAttempts
        }
        return confidence.coerceIn(0f, 1f)
    }
}
