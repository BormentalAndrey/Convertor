package com.example.russianpath.data.repository

import com.example.russianpath.data.local.dao.UserProgressDao
import com.example.russianpath.data.local.entity.UserProgressEntity
import com.example.russianpath.data.local.entity.LessonCompletionEntity
import com.example.russianpath.domain.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userProgressDao: UserProgressDao
) {
    fun getUserStats(): Flow<UserStats?> {
        return userProgressDao.getUserProgress().map { entity ->
            entity?.toDomainModel()
        }
    }
    
    suspend fun addXp(amount: Int) {
        userProgressDao.addXp(amount)
    }
    
    suspend fun addGems(amount: Int) {
        userProgressDao.addGems(amount)
    }
    
    suspend fun loseLife() {
        val progress = userProgressDao.getUserProgress().first()
        if (progress != null) {
            val newLives = maxOf(0, progress.livesCount - 1)
            userProgressDao.updateLives(newLives)
        }
    }
    
    suspend fun completeLesson(
        lessonId: String,
        stars: Int,
        mistakesCount: Int,
        xpEarned: Int
    ) {
        val completion = LessonCompletionEntity(
            lessonId = lessonId,
            stars = stars,
            mistakesCount = mistakesCount,
            completedAt = System.currentTimeMillis(),
            xpEarned = xpEarned
        )
        userProgressDao.saveLessonCompletion(completion)
        userProgressDao.addXp(xpEarned)
    }
}

private fun UserProgressEntity.toDomainModel(): UserStats {
    return UserStats(
        totalXp = totalXp,
        level = (totalXp / 100) + 1,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        gemsBalance = gemsBalance,
        livesCount = livesCount,
        // ИСПРАВЛЕНО: Заменено на totalLessonsCompleted (должно совпадать с полем в БД)
        totalLessonsCompleted = totalLessonsCompleted 
    )
}
