package com.example.russianpath.core.repository

import com.example.russianpath.core.knowledge.LearningObjective
import com.example.russianpath.core.knowledge.MicroSkill
import com.example.russianpath.core.knowledge.SkillCode

interface KnowledgeRepository {
    suspend fun getObjectiveById(objectiveId: String): LearningObjective
    suspend fun getMicroSkillsByObjective(objectiveId: String): List<MicroSkill>
    suspend fun getPrerequisitesBySkill(skillCode: SkillCode): List<SkillCode>
}
