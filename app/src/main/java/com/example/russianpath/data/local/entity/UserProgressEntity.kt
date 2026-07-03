package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Общий прогресс пользователя. Синглтон-запись (id = 1).
 * Хранит агрегированные показатели для быстрого доступа без вычислений.
 */
@Entity(
    tableName = "user_progress",
    indices = [
        Index(
            value = ["last_active_date"],
            name = "idx_user_progress_last_active"
        )
    ]
)
data class UserProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,

    @ColumnInfo(name = "total_xp", defaultValue = "0")
    val totalXp: Int = 0,

    @ColumnInfo(name = "current_level", defaultValue = "1")
    val currentLevel: Int = 1,

    @ColumnInfo(name = "xp_to_next_level", defaultValue = "100")
    val xpToNextLevel: Int = 100,

    @ColumnInfo(name = "current_streak", defaultValue = "0")
    val currentStreak: Int = 0,

    @ColumnInfo(name = "longest_streak", defaultValue = "0")
    val longestStreak: Int = 0,

    @ColumnInfo(name = "last_active_date", defaultValue = "0")
    val lastActiveDate: Long = 0L,

    @ColumnInfo(name = "streak_start_date", defaultValue = "0")
    val streakStartDate: Long = 0L,

    @ColumnInfo(name = "gems_balance", defaultValue = "50")
    val gemsBalance: Int = 50,

    @ColumnInfo(name = "lives_count", defaultValue = "5")
    val livesCount: Int = 5,

    @ColumnInfo(name = "max_lives", defaultValue = "5")
    val maxLives: Int = 5,

    @ColumnInfo(name = "last_life_refill_time", defaultValue = "0")
    val lastLifeRefillTime: Long = 0L,

    @ColumnInfo(name = "total_lessons_completed", defaultValue = "0")
    val totalLessonsCompleted: Int = 0,

    @ColumnInfo(name = "total_mistakes_count", defaultValue = "0")
    val totalMistakesCount: Int = 0,

    @ColumnInfo(name = "total_time_spent_seconds", defaultValue = "0")
    val totalTimeSpentSeconds: Long = 0L,

    @ColumnInfo(name = "total_perfect_lessons", defaultValue = "0")
    val totalPerfectLessons: Int = 0,

    @ColumnInfo(name = "total_days_active", defaultValue = "0")
    val totalDaysActive: Int = 0,

    @ColumnInfo(name = "current_grade_id", defaultValue = "")
    val currentGradeId: String = "",

    @ColumnInfo(name = "current_topic_id", defaultValue = "")
    val currentTopicId: String = "",

    @ColumnInfo(name = "onboarding_completed", defaultValue = "0")
    val onboardingCompleted: Boolean = false,

    @ColumnInfo(name = "last_sync_time", defaultValue = "0")
    val lastSyncTime: Long = 0L,

    @ColumnInfo(name = "schema_version", defaultValue = "1")
    val schemaVersion: Int = 1,

    @ColumnInfo(name = "created_at", defaultValue = "0")
    val createdAt: Long = 0L,

    @ColumnInfo(name = "updated_at", defaultValue = "0")
    val updatedAt: Long = 0L
)
