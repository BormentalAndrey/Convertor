package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Микро-навыки — атомарные единицы знания.
 * Пример: "Определение приставки", "Выделение корня", "Проверка ударением".
 * Связаны с LearningObjectiveEntity.
 */
@Entity(
    tableName = "micro_skills",
    foreignKeys = [
        ForeignKey(
            entity = LearningObjectiveEntity::class,
            parentColumns = ["id"],
            childColumns = ["objective_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["objective_id", "sort_order"],
            name = "idx_micro_skills_objective_sort"
        ),
        Index(
            value = ["skill_code_id"],
            name = "idx_micro_skills_skill_code"
        ),
        Index(
            value = ["external_id"],
            name = "idx_micro_skills_external_id",
            unique = true
        ),
        Index(
            value = ["objective_id"],
            name = "idx_micro_skills_objective_id"
        )
    ]
)
data class MicroSkillEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "external_id", defaultValue = "")
    val externalId: String = "",

    @ColumnInfo(name = "objective_id")
    val objectiveId: String,

    @ColumnInfo(name = "skill_code_id")
    val skillCodeId: Int,

    @ColumnInfo(name = "parent_micro_skill_id", defaultValue = "")
    val parentMicroSkillId: String = "",

    @ColumnInfo(name = "name", defaultValue = "")
    val name: String,

    @ColumnInfo(name = "description", defaultValue = "")
    val description: String = "",

    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int,

    @ColumnInfo(name = "difficulty_level", defaultValue = "1")
    val difficultyLevel: Int = 1,

    @ColumnInfo(name = "error_category", defaultValue = "")
    val errorCategory: String = "",

    @ColumnInfo(name = "typical_mistake_pattern_json", defaultValue = "[]")
    val typicalMistakePatternJson: String = "[]",

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
