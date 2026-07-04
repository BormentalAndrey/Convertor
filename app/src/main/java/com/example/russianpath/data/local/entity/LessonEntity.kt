// app/src/main/java/com/example/russianpath/data/local/entity/LessonEntity.kt

package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

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
    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerializedName("external_id")
    @ColumnInfo(name = "external_id", defaultValue = "")
    val externalId: String = "",

    @SerializedName("topic_id")
    @ColumnInfo(name = "topic_id")
    val topicId: String,

    // ИСПРАВЛЕНО: Сделано nullable (String?), чтобы соответствовать ForeignKey.SET_NULL
    @SerializedName("primary_objective_id")
    @ColumnInfo(name = "primary_objective_id", defaultValue = "NULL")
    val primaryObjectiveId: String? = null,

    @SerializedName("lesson_type")
    @ColumnInfo(name = "lesson_type", defaultValue = "practice")
    val lessonType: String,

    @SerializedName("title")
    @ColumnInfo(name = "title", defaultValue = "")
    val title: String,

    @SerializedName("description")
    @ColumnInfo(name = "description", defaultValue = "")
    val description: String = "",

    @SerializedName("instruction_text")
    @ColumnInfo(name = "instruction_text", defaultValue = "")
    val instructionText: String = "",

    @SerializedName("difficulty")
    @ColumnInfo(name = "difficulty", defaultValue = "1")
    val difficulty: Int,

    @SerializedName("sort_order")
    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int,

    @SerializedName("theory_json")
    @ColumnInfo(name = "theory_json", defaultValue = "{}")
    val theoryJson: String = "{}",

    @SerializedName("questions_count")
    @ColumnInfo(name = "questions_count", defaultValue = "0")
    val questionsCount: Int = 0,

    @SerializedName("time_limit_seconds")
    @ColumnInfo(name = "time_limit_seconds", defaultValue = "0")
    val timeLimitSeconds: Int = 0,

    @SerializedName("passing_score_percent")
    @ColumnInfo(name = "passing_score_percent", defaultValue = "70")
    val passingScorePercent: Int = 70,

    @SerializedName("max_stars")
    @ColumnInfo(name = "max_stars", defaultValue = "3")
    val maxStars: Int = 3,

    @SerializedName("xp_base_reward")
    @ColumnInfo(name = "xp_base_reward", defaultValue = "50")
    val xpBaseReward: Int = 50,

    @SerializedName("xp_perfect_bonus")
    @ColumnInfo(name = "xp_perfect_bonus", defaultValue = "25")
    val xpPerfectBonus: Int = 25,

    @SerializedName("gems_reward")
    @ColumnInfo(name = "gems_reward", defaultValue = "5")
    val gemsReward: Int = 5,

    @SerializedName("is_bonus")
    @ColumnInfo(name = "is_bonus", defaultValue = "0")
    val isBonus: Boolean = false,

    @SerializedName("is_diagnostic")
    @ColumnInfo(name = "is_diagnostic", defaultValue = "0")
    val isDiagnostic: Boolean = false,

    @SerializedName("is_active")
    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    @SerializedName("schema_version")
    @ColumnInfo(name = "schema_version", defaultValue = "1")
    val schemaVersion: Int = 1,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at", defaultValue = "0")
    val createdAt: Long = 0L,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at", defaultValue = "0")
    val updatedAt: Long = 0L,

    @SerializedName("server_updated_at")
    @ColumnInfo(name = "server_updated_at", defaultValue = "0")
    val serverUpdatedAt: Long = 0L
)
