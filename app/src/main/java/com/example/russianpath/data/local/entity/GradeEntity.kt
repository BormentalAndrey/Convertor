package com.example.russianpath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grades")
data class GradeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val sortOrder: Int
)
