package com.example.russianpath.data.local.converter

import androidx.room.TypeConverter
import com.example.russianpath.core.exercise.Difficulty
import com.example.russianpath.core.knowledge.SkillCode

class Converters {

    // SkillCode ↔ Int
    @TypeConverter
    fun fromSkillCode(skillCode: SkillCode): Int = skillCode.code

    @TypeConverter
    fun toSkillCode(code: Int): SkillCode = SkillCode.entries.first { it.code == code }

    // Difficulty ↔ Int
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): Int = difficulty.level

    @TypeConverter
    fun toDifficulty(level: Int): Difficulty = Difficulty(level)
}
