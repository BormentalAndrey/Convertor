package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "topics_v2",
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sectionId")]
)
data class TopicEntity(
    @PrimaryKey
    val id: String,
    val sectionId: String,
    val name: String,
    val description: String?,
    val sortOrder: Int,
    val difficulty: Int = 1
)
