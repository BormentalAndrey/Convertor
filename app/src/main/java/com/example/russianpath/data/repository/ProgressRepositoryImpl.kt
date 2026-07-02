package com.example.russianpath.data.repository

import com.example.russianpath.core.progress.AnswerResult
import com.example.russianpath.core.progress.Mastery
import com.example.russianpath.core.repository.ProgressRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor() : ProgressRepository {

    // Временное in-memory хранилище для v1.0
    private val masteryMap = mutableMapOf<String, Mastery>()

    override suspend fun recordAnswer(result: AnswerResult) {
        // В v1.0 просто логируем результат
        // В v2.0 добавится таблица answer_history и обновление mastery
        android.util.Log.d(
            "ProgressRepository",
            "Answer: exerciseId=${result.exerciseId}, " +
            "correct=${result.isCorrect}, " +
            "time=${result.timeSpentMs}ms"
        )
    }

    override suspend fun getMastery(microSkillId: String): Mastery {
        return masteryMap[ microSkillId] ?: Mastery(
            microSkillId = microSkillId,
            skillCode = com.example.russianpath.core.knowledge.SkillCode.COUNT_SYLLABLES,
            level = 0f,
            confidence = 0f,
            totalAttempts = 0,
            correctAttempts = 0,
            lastReviewed = java.time.Instant.now()
        )
    }
}
