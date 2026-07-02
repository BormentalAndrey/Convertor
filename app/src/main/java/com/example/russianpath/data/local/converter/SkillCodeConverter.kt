package com.example.russianpath.data.local.converter

import androidx.room.TypeConverter
import com.example.russianpath.core.knowledge.SkillCode

class SkillCodeConverter {

    @TypeConverter
    fun fromSkillCode(value: SkillCode): Int = value.code

    @TypeConverter
    fun toSkillCode(value: Int): SkillCode = SkillCode.fromCode(value)
}
