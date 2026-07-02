package com.example.russianpath.core.repository

import com.example.russianpath.core.knowledge.*

interface KnowledgeRepository {
    suspend fun getObjectiveById(id: ObjectiveId): LearningObjective
    suspend fun getMicroSkillsByObjective(objectiveId: ObjectiveId): List<MicroSkill>
    suspend fun getPrerequisitesBySkill(skillCode: SkillCode): List<SkillCode>
}
