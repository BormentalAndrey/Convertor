package com.example.russianpath.data.local.mapper

import com.example.russianpath.core.knowledge.LearningObjective
import com.example.russianpath.core.knowledge.MicroSkill
import com.example.russianpath.core.knowledge.SkillCode
import com.example.russianpath.data.local.entity.LearningObjectiveEntity
import com.example.russianpath.data.local.entity.MicroSkillEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnowledgeMapper @Inject constructor() {

    private val gson = Gson()

    fun toDomain(entity: LearningObjectiveEntity): LearningObjective {
        val prerequisites = gson.fromJson<List<String>>(
            entity.prerequisitesJson,
            object : TypeToken<List<String>>() {}.type
        )
        return LearningObjective(
            id = entity.id,
            name = entity.name,
            description = entity.description ?: "",
            prerequisites = prerequisites
        )
    }

    fun toDomain(entity: MicroSkillEntity): MicroSkill {
        return MicroSkill(
            id = entity.id,
            objectiveId = entity.objectiveId,
            skillCode = SkillCode.fromCode(entity.skillCodeId),
            name = entity.name,
            description = entity.description ?: ""
        )
    }
}
