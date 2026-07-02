package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "learning_objectives",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("topicId")]
)
data class LearningObjectiveEntity(
    @PrimaryKey
    val id: String,
    val topicId: String,
    val name: String,
    val description: String?,
    val sortOrder: Int,
    val prerequisitesJson: String = "[]"
)
