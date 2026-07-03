package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Темы внутри раздела (например, "Правописание приставок ПРЕ-/ПРИ-").
 * Основная единица контента, вокруг которой строится обучение.
 */
@Entity(
    tableName = "topics",
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["section_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GradeEntity::class,
            parentColumns = ["id"],
            childColumns = ["grade_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["section_id", "sort_order"],
            name = "idx_topics_section_sort"
        ),
        Index(
            value = ["grade_id", "sort_order"],
            name = "idx_topics_grade_sort"
        ),
        Index(
            value = ["external_id"],
            name = "idx_topics_external_id",
            unique = true
        ),
        Index(
            value = ["section_id"],
            name = "idx_topics_section_id"
        ),
        Index(
            value = ["grade_id"],
            name = "idx_topics_grade_id"
        ),
        Index(
            value = ["is_active", "is_unlocked"],
            name = "idx_topics_active_unlocked"
        )
    ]
)
data class TopicEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "external_id", defaultValue = "")
    val externalId: String = "",

    @ColumnInfo(name = "section_id")
    val sectionId: String,

    @ColumnInfo(name = "grade_id", defaultValue = "")
    val gradeId: String = "",

    @ColumnInfo(name = "title", defaultValue = "")
    val title: String,

    @ColumnInfo(name = "description", defaultValue = "")
    val description: String,

    @ColumnInfo(name = "icon_name", defaultValue = "")
    val iconName: String = "",

    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int,

    @ColumnInfo(name = "is_unlocked", defaultValue = "0")
    val isUnlocked: Boolean = false,

    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    @ColumnInfo(name = "difficulty_level", defaultValue = "1")
    val difficultyLevel: Int = 1,

    @ColumnInfo(name = "estimated_minutes", defaultValue = "15")
    val estimatedMinutes: Int = 15,

    @ColumnInfo(name = "prerequisite_topic_ids_json", defaultValue = "[]")
    val prerequisiteTopicIdsJson: String = "[]",

    @ColumnInfo(name = "schema_version", defaultValue = "1")
    val schemaVersion: Int = 1,

    @ColumnInfo(name = "content_hash", defaultValue = "")
    val contentHash: String = "",

    @ColumnInfo(name = "created_at", defaultValue = "0")
    val createdAt: Long = 0L,

    @ColumnInfo(name = "updated_at", defaultValue = "0")
    val updatedAt: Long = 0L,

    @ColumnInfo(name = "server_updated_at", defaultValue = "0")
    val serverUpdatedAt: Long = 0L
)
