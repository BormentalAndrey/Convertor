package com.example.russianpath.data.mapper

import com.example.russianpath.core.knowledge.LearningObjective
import com.example.russianpath.core.knowledge.MicroSkill
import com.example.russianpath.core.knowledge.SkillCode
import com.example.russianpath.data.local.entity.LearningObjectiveEntity
import com.example.russianpath.data.local.entity.MicroSkillEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun LearningObjectiveEntity.toDomain(): LearningObjective {
    val prerequisites: List<String> = try {
        Gson().fromJson(prerequisitesJson, object : TypeToken<List<String>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }

    return LearningObjective(
        id = id,
        name = name,
        description = description ?: "",
        prerequisites = prerequisites
    )
}

fun MicroSkillEntity.toDomain(): MicroSkill {
    return MicroSkill(
        id = id,
        objectiveId = objectiveId,
        skillCode = SkillCode.entries.first { it.code == skillCodeId },
        name = name,
        description = description ?: ""
    )
}
