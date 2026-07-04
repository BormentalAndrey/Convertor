// app/src/main/java/com/example/russianpath/data/local/entity/SectionEntity.kt

package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sections",
    foreignKeys = [
        ForeignKey(entity = GradeEntity::class, parentColumns = ["id"], childColumns = ["grade_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["grade_id", "sort_order"], name = "idx_sections_grade_sort"),
        Index(value = ["external_id"], name = "idx_sections_external_id", unique = true),
        Index(value = ["grade_id"], name = "idx_sections_grade_id")
    ]
)
data class SectionEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "external_id", defaultValue = "") val externalId: String = "",
    @ColumnInfo(name = "grade_id") val gradeId: String,
    @ColumnInfo(name = "name", defaultValue = "") val name: String = "",
    @ColumnInfo(name = "display_name", defaultValue = "") val displayName: String = "",
    @ColumnInfo(name = "description", defaultValue = "") val description: String = "",
    @ColumnInfo(name = "sort_order", defaultValue = "0") val sortOrder: Int = 0,
    @ColumnInfo(name = "icon_name", defaultValue = "") val iconName: String = "",
    @ColumnInfo(name = "color_hex", defaultValue = "#FF6200EE") val colorHex: String = "#FF6200EE",
    @ColumnInfo(name = "is_active", defaultValue = "1") val isActive: Boolean = true,
    @ColumnInfo(name = "schema_version", defaultValue = "1") val schemaVersion: Int = 1,
    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = 0L,
    @ColumnInfo(name = "updated_at", defaultValue = "0") val updatedAt: Long = 0L,
    @ColumnInfo(name = "server_updated_at", defaultValue = "0") val serverUpdatedAt: Long = 0L
)
