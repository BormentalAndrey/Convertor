// app/src/main/java/com/example/russianpath/data/local/entity/GradeEntity.kt

package com.example.russianpath.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "grades",
    indices = [
        Index(value = ["sort_order"], name = "idx_grades_sort_order"),
        Index(value = ["external_id"], name = "idx_grades_external_id", unique = true)
    ]
)
data class GradeEntity(
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerializedName("external_id")
    @ColumnInfo(name = "external_id", defaultValue = "")
    val externalId: String = "",

    @SerializedName("name")
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String = "",

    @SerializedName("display_name")
    @ColumnInfo(name = "display_name", defaultValue = "")
    val displayName: String = "",

    @SerializedName("description")
    @ColumnInfo(name = "description", defaultValue = "")
    val description: String = "",

    @SerializedName("sort_order")
    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int = 0,

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
