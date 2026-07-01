package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val gradeLevel: Int,
    val title: String,
    val description: String,
    val sortOrder: Int,
    val prerequisiteTopicId: String? = null,
    val isUnlocked: Boolean = false
)
