// app/src/main/java/com/example/russianpath/data/local/entity/LessonEntity.kt

package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LearningObjectiveEntity::class,
            parentColumns = ["id"],
            childColumns = ["primary_objective_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["topic_id", "sort_order"], name = "idx_lessons_topic_sort"),
        Index(value = ["topic_id", "lesson_type", "sort_order"], name = "idx_lessons_topic_type_sort"),
        Index(value = ["external_id"], name = "idx_lessons_external_id", unique = true),
        Index(value = ["topic_id"], name = "idx_lessons_topic_id"),
        Index(value = ["primary_objective_id"], name = "idx_lessons_objective_id"),
        Index(value = ["is_active"], name = "idx_lessons_active")
    ]
)
data class LessonEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "external_id", defaultValue = "")
    val externalId: String = "",

    @ColumnInfo(name = "topic_id")
    val topicId: String,

    @ColumnInfo(name = "primary_objective_id")
    val primaryObjectiveId: String? = null,

    @ColumnInfo(name = "lesson_type", defaultValue = "practice")
    val lessonType: String = "practice",

    @ColumnInfo(name = "title", defaultValue = "")
    val title: String = "",

    @ColumnInfo(name = "description", defaultValue = "")
    val description: String = "",

    @ColumnInfo(name = "instruction_text", defaultValue = "")
    val instructionText: String = "",

    @ColumnInfo(name = "exercise_text_json", defaultValue = "{}")
    val exerciseTextJson: String = "{}",

    @ColumnInfo(name = "difficulty", defaultValue = "1")
    val difficulty: Int = 1,

    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "theory_json", defaultValue = "{}")
    val theoryJson: String = "{}",

    @ColumnInfo(name = "questions_count", defaultValue = "0")
    val questionsCount: Int = 0,

    @ColumnInfo(name = "time_limit_seconds", defaultValue = "0")
    val timeLimitSeconds: Int = 0,

    @ColumnInfo(name = "passing_score_percent", defaultValue = "70")
    val passingScorePercent: Int = 70,

    @ColumnInfo(name = "max_stars", defaultValue = "3")
    val maxStars: Int = 3,

    @ColumnInfo(name = "xp_base_reward", defaultValue = "50")
    val xpBaseReward: Int = 50,

    @ColumnInfo(name = "xp_perfect_bonus", defaultValue = "25")
    val xpPerfectBonus: Int = 25,

    @ColumnInfo(name = "gems_reward", defaultValue = "5")
    val gemsReward: Int = 5,

    @ColumnInfo(name = "is_bonus", defaultValue = "0")
    val isBonus: Boolean = false,

    @ColumnInfo(name = "is_diagnostic", defaultValue = "0")
    val isDiagnostic: Boolean = false,

    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    @ColumnInfo(name = "schema_version", defaultValue = "1")
    val schemaVersion: Int = 1,

    @ColumnInfo(name = "created_at", defaultValue = "0")
    val createdAt: Long = 0L,

    @ColumnInfo(name = "updated_at", defaultValue = "0")
    val updatedAt: Long = 0L,

    @ColumnInfo(name = "server_updated_at", defaultValue = "0")
    val serverUpdatedAt: Long = 0L
)
