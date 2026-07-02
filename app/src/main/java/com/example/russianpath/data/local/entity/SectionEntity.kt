package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sections",
    foreignKeys = [
        ForeignKey(
            entity = GradeEntity::class,
            parentColumns = ["id"],
            childColumns = ["gradeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("gradeId")]
)
data class SectionEntity(
    @PrimaryKey
    val id: String,
    val gradeId: String,
    val name: String,
    val sortOrder: Int
)
