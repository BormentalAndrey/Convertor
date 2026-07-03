package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Цели обучения внутри темы.
 * Пример: "Научиться различать приставки ПРЕ- и ПРИ- по значению".
 * Связаны с TopicEntity.
 */
@Entity(
    tableName = "learning_objectives",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["topic_id", "sort_order"],
            name = "idx_objectives_topic_sort"
        ),
        Index(
            value = ["external_id"],
            name = "idx_objectives_external_id",
            unique = true
        ),
        Index(
            value = ["topic_id"],
            name = "idx_objectives_topic_id"
        ),
        Index(
            value = ["skill_code_id"],
            name = "idx_objectives_skill_code"
        )
    ]
)
data class LearningObjectiveEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "external_id", defaultValue = "")
    val externalId: String = "",

    @ColumnInfo(name = "topic_id")
    val topicId: String,

    @ColumnInfo(name = "skill_code_id", defaultValue = "0")
    val skillCodeId: Int = 0,

    @ColumnInfo(name = "name", defaultValue = "")
    val name: String,

    @ColumnInfo(name = "description", defaultValue = "")
    val description: String = "",

    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int,

    @ColumnInfo(name = "prerequisite_objective_ids_json", defaultValue = "[]")
    val prerequisiteObjectiveIdsJson: String = "[]",

    @ColumnInfo(name = "bloom_taxonomy_level", defaultValue = "1")
    val bloomTaxonomyLevel: Int = 1,

    @ColumnInfo(name = "mastery_threshold_percent", defaultValue = "80")
    val masteryThresholdPercent: Int = 80,

    @ColumnInfo(name = "is_required", defaultValue = "1")
    val isRequired: Boolean = true,

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
