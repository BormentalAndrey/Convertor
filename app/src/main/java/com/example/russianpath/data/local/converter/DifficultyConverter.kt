package com.example.russianpath.data.local.converter

import androidx.room.TypeConverter
import com.example.russianpath.core.common.Difficulty

class DifficultyConverter {

    @TypeConverter
    fun fromDifficulty(value: Difficulty): Int = value.value

    @TypeConverter
    fun toDifficulty(value: Int): Difficulty = Difficulty(value)
}
