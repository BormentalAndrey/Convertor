package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "micro_skills",
    foreignKeys = [
        ForeignKey(
            entity = LearningObjectiveEntity::class,
            parentColumns = ["id"],
            childColumns = ["objectiveId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("objectiveId")]
)
data class MicroSkillEntity(
    @PrimaryKey
    val id: String,
    val objectiveId: String,
    val skillCodeId: Int,
    val name: String,
    val description: String?
)
