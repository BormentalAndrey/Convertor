package com.example.russianpath.core.repository

import com.example.russianpath.core.progress.AnswerResult
import com.example.russianpath.core.progress.Mastery

interface ProgressRepository {
    suspend fun recordAnswer(result: AnswerResult)
    suspend fun getMastery(microSkillId: String): Mastery
}
