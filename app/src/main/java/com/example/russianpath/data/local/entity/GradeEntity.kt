package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Классы обучения: 1–11 класс, ОГЭ, ЕГЭ.
 * Рассчитан на долгосрочное использование без изменения структуры.
 */
@Entity(
    tableName = "grades",
    indices = [
        androidx.room.Index(
            value = ["sort_order"],
            name = "idx_grades_sort_order"
        ),
        androidx.room.Index(
            value = ["external_id"],
            name = "idx_grades_external_id",
            unique = true
        )
    ]
)
data class GradeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "external_id", defaultValue = "")
    val externalId: String = "",

    @ColumnInfo(name = "name", defaultValue = "")
    val name: String,

    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int,

    @ColumnInfo(name = "display_name", defaultValue = "")
    val displayName: String = "",

    @ColumnInfo(name = "description", defaultValue = "")
    val description: String = "",

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
