// app/src/main/java/com/example/russianpath/data/local/entity/RuleEntity.kt

package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность правила орфографии/пунктуации/грамматики.
 *
 * Содержит полное описание правила с примерами и исключениями.
 * Связана с TopicEntity (тема) и GradeEntity (класс).
 *
 * Связи:
 * - topic_id → TopicEntity.id (CASCADE при удалении темы)
 * - grade_id → GradeEntity.id (CASCADE при удалении класса)
 *
 * Для production: все JSON-файлы должны использовать одинаковые ID.
 * Правила привязываются к темам, темы — к разделам, разделы — к классам.
 */
@Entity(
    tableName = "rules",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
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
        Index(value = ["topic_id"], name = "idx_rules_topic_id"),
        Index(value = ["grade_id"], name = "idx_rules_grade_id"),
        Index(value = ["external_id"], name = "idx_rules_external_id", unique = true),
        Index(value = ["rule_category", "grade_id"], name = "idx_rules_category_grade")
    ]
)
data class RuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "external_id", defaultValue = "")
    val externalId: String = "",

    @ColumnInfo(name = "topic_id", defaultValue = "")
    val topicId: String = "",

    @ColumnInfo(name = "grade_id", defaultValue = "")
    val gradeId: String = "",

    @ColumnInfo(name = "title", defaultValue = "")
    val title: String = "",

    @ColumnInfo(name = "short_description", defaultValue = "")
    val shortDescription: String = "",

    @ColumnInfo(name = "full_description", defaultValue = "")
    val fullDescription: String = "",

    @ColumnInfo(name = "rule_text", defaultValue = "")
    val ruleText: String = "",

    @ColumnInfo(name = "examples_json", defaultValue = "[]")
    val examplesJson: String = "[]",

    @ColumnInfo(name = "counterexamples_json", defaultValue = "[]")
    val counterexamplesJson: String = "[]",

    @ColumnInfo(name = "exceptions_json", defaultValue = "[]")
    val exceptionsJson: String = "[]",

    @ColumnInfo(name = "rule_category", defaultValue = "")
    val ruleCategory: String = "",

    @ColumnInfo(name = "difficulty_level", defaultValue = "1")
    val difficultyLevel: Int = 1,

    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "icon_name", defaultValue = "")
    val iconName: String = "",

    @ColumnInfo(name = "related_rule_ids_json", defaultValue = "[]")
    val relatedRuleIdsJson: String = "[]",

    @ColumnInfo(name = "mnemonic_text", defaultValue = "")
    val mnemonicText: String = "",

    @ColumnInfo(name = "video_url", defaultValue = "")
    val videoUrl: String = "",

    @ColumnInfo(name = "image_path", defaultValue = "")
    val imagePath: String = "",

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
