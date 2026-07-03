package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Запись о завершении урока пользователем.
 * Хранит результаты попытки для аналитики и интервального повторения.
 */
@Entity(
    tableName = "lesson_completions",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["lesson_id", "completed_at"],
            name = "idx_completions_lesson_date"
        ),
        Index(
            value = ["completed_at"],
            name = "idx_completions_date"
        ),
        Index(
            value = ["lesson_id"],
            name = "idx_completions_lesson_id"
        ),
        Index(
            value = ["topic_id", "completed_at"],
            name = "idx_completions_topic_date"
        )
    ]
)
data class LessonCompletionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "lesson_id")
    val lessonId: String,

    @ColumnInfo(name = "topic_id", defaultValue = "")
    val topicId: String = "",

    @ColumnInfo(name = "stars", defaultValue = "0")
    val stars: Int = 0,

    @ColumnInfo(name = "score_percent", defaultValue = "0")
    val scorePercent: Int = 0,

    @ColumnInfo(name = "correct_answers", defaultValue = "0")
    val correctAnswers: Int = 0,

    @ColumnInfo(name = "total_questions", defaultValue = "0")
    val totalQuestions: Int = 0,

    @ColumnInfo(name = "mistakes_count", defaultValue = "0")
    val mistakesCount: Int = 0,

    @ColumnInfo(name = "mistakes_json", defaultValue = "[]")
    val mistakesJson: String = "[]",

    @ColumnInfo(name = "time_spent_seconds", defaultValue = "0")
    val timeSpentSeconds: Int = 0,

    @ColumnInfo(name = "completed_at", defaultValue = "0")
    val completedAt: Long = 0L,

    @ColumnInfo(name = "xp_earned", defaultValue = "0")
    val xpEarned: Int = 0,

    @ColumnInfo(name = "gems_earned", defaultValue = "0")
    val gemsEarned: Int = 0,

    @ColumnInfo(name = "attempt_number", defaultValue = "1")
    val attemptNumber: Int = 1,

    @ColumnInfo(name = "is_passed", defaultValue = "0")
    val isPassed: Boolean = false,

    @ColumnInfo(name = "device_id", defaultValue = "")
    val deviceId: String = "",

    @ColumnInfo(name = "schema_version", defaultValue = "1")
    val schemaVersion: Int = 1,

    @ColumnInfo(name = "synced_at", defaultValue = "0")
    val syncedAt: Long = 0L
)
