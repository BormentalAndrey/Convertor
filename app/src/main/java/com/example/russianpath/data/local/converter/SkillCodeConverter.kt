package com.example.russianpath.data.local.converter

import androidx.room.TypeConverter
import com.example.russianpath.core.common.Difficulty
import com.example.russianpath.core.knowledge.SkillCode

class SkillCodeConverter {

    @TypeConverter
    fun fromSkillCode(skillCode: SkillCode): Int = skillCode.code

    @TypeConverter
    fun toSkillCode(code: Int): SkillCode = SkillCode.entries.first { it.code == code }

    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): Int = difficulty.value

    @TypeConverter
    fun toDifficulty(value: Int): Difficulty = Difficulty(value)
}
