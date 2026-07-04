// app/src/main/java/com/example/russianpath/data/repository/ProgressRepositoryImpl.kt

package com.example.russianpath.data.repository

import com.example.russianpath.core.knowledge.MicroSkillId
import com.example.russianpath.core.progress.AnswerResult
import com.example.russianpath.core.progress.Mastery
import com.example.russianpath.core.repository.ProgressRepository
import com.example.russianpath.data.local.converter.SkillCode
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor() : ProgressRepository {

    private val masteryStore = mutableMapOf<MicroSkillId, Mastery>()

    override suspend fun recordAnswer(result: AnswerResult) {
        val microSkillId = MicroSkillId("ms_${result.skillCode.code}")

        val current = masteryStore[microSkillId] ?: Mastery(
            microSkillId = microSkillId,
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
            confidence = calculateConfidence(current),
            lastReviewed = Instant.now()
        )

        masteryStore[microSkillId] = updated
    }

    override suspend fun getMastery(microSkillId: MicroSkillId): Mastery {
        return masteryStore[microSkillId] ?: Mastery(
            microSkillId = microSkillId,
            skillCode = SkillCode.UNKNOWN,
            level = 0f,
            confidence = 0f,
            totalAttempts = 0,
            correctAttempts = 0,
            lastReviewed = Instant.now()
        )
    }

    private fun calculateLevel(current: Mastery, isCorrect: Boolean): Float {
        val total = current.totalAttempts + 1
        val correct = if (isCorrect) current.correctAttempts + 1 else current.correctAttempts
        return (correct.toFloat() / total).coerceIn(0f, 1f)
    }

    private fun calculateConfidence(current: Mastery): Float {
        if (current.totalAttempts < 3) return 0.5f
        return (current.correctAttempts.toFloat() / current.totalAttempts).coerceIn(0f, 1f)
    }
}
