// app/src/main/java/com/example/russianpath/data/local/entity/QuestionEntity.kt

package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(entity = LessonEntity::class, parentColumns = ["id"], childColumns = ["lesson_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = MicroSkillEntity::class, parentColumns = ["id"], childColumns = ["primary_skill_id"], onDelete = ForeignKey.SET_NULL, onUpdate = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["lesson_id", "sort_order"], name = "idx_questions_lesson_sort"),
        Index(value = ["lesson_id", "question_type"], name = "idx_questions_lesson_type"),
        Index(value = ["external_id"], name = "idx_questions_external_id", unique = true),
        Index(value = ["lesson_id"], name = "idx_questions_lesson_id"),
        Index(value = ["primary_skill_id"], name = "idx_questions_skill_id"),
        Index(value = ["question_type"], name = "idx_questions_type"),
        Index(value = ["difficulty"], name = "idx_questions_difficulty")
    ]
)
data class QuestionEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "external_id", defaultValue = "") val externalId: String = "",
    @ColumnInfo(name = "lesson_id") val lessonId: String,
    @ColumnInfo(name = "primary_skill_id", defaultValue = "") val primarySkillId: String = "",
    @ColumnInfo(name = "question_type", defaultValue = "single_choice") val questionType: String = "single_choice",
    @ColumnInfo(name = "prompt_text", defaultValue = "") val promptText: String = "",
    @ColumnInfo(name = "prompt_audio_path", defaultValue = "") val promptAudioPath: String = "",
    @ColumnInfo(name = "prompt_image_path", defaultValue = "") val promptImagePath: String = "",
    @ColumnInfo(name = "data_json", defaultValue = "{}") val dataJson: String = "{}",
    @ColumnInfo(name = "correct_answer_json", defaultValue = "{}") val correctAnswerJson: String = "{}",
    @ColumnInfo(name = "acceptable_answers_json", defaultValue = "[]") val acceptableAnswersJson: String = "[]",
    @ColumnInfo(name = "hint_text", defaultValue = "") val hintText: String = "",
    @ColumnInfo(name = "explanation_text", defaultValue = "") val explanationText: String = "",
    @ColumnInfo(name = "audio_path", defaultValue = "") val audioPath: String = "",
    @ColumnInfo(name = "rule_reference", defaultValue = "") val ruleReference: String = "",
    @ColumnInfo(name = "rule_reference_id", defaultValue = "") val ruleReferenceId: String = "",
    @ColumnInfo(name = "difficulty", defaultValue = "1") val difficulty: Int = 1,
    @ColumnInfo(name = "sort_order", defaultValue = "0") val sortOrder: Int = 0,
    @ColumnInfo(name = "time_limit_seconds", defaultValue = "0") val timeLimitSeconds: Int = 0,
    @ColumnInfo(name = "points", defaultValue = "10") val points: Int = 10,
    @ColumnInfo(name = "penalty_points", defaultValue = "0") val penaltyPoints: Int = 0,
    @ColumnInfo(name = "max_attempts", defaultValue = "0") val maxAttempts: Int = 0,
    @ColumnInfo(name = "is_required", defaultValue = "1") val isRequired: Boolean = true,
    @ColumnInfo(name = "is_active", defaultValue = "1") val isActive: Boolean = true,
    @ColumnInfo(name = "schema_version", defaultValue = "1") val schemaVersion: Int = 1,
    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = 0L,
    @ColumnInfo(name = "updated_at", defaultValue = "0") val updatedAt: Long = 0L,
    @ColumnInfo(name = "server_updated_at", defaultValue = "0") val serverUpdatedAt: Long = 0L
)
