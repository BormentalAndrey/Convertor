package com.example.russianpath.data.repository

import com.example.russianpath.core.knowledge.LearningObjective
import com.example.russianpath.core.knowledge.MicroSkill
import com.example.russianpath.core.knowledge.MicroSkillId
import com.example.russianpath.core.knowledge.ObjectiveId
import com.example.russianpath.core.knowledge.SkillCode
import com.example.russianpath.core.repository.KnowledgeRepository
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.mapper.KnowledgeMapper
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnowledgeRepositoryImpl @Inject constructor(
    private val learningObjectiveDao: LearningObjectiveDao,
    private val microSkillDao: MicroSkillDao,
    private val knowledgeMapper: KnowledgeMapper
) : KnowledgeRepository {

    override suspend fun getObjectiveById(id: ObjectiveId): LearningObjective {
        val entity = learningObjectiveDao.getById(id.value)
            ?: throw IllegalArgumentException("Objective not found: $id")
        return knowledgeMapper.toDomain(entity)
    }

    override suspend fun getMicroSkillsByObjective(objectiveId: ObjectiveId): List<MicroSkill> {
        return microSkillDao
            .observeByObjective(objectiveId.value)
            .first()
            .map { knowledgeMapper.toDomain(it) }
    }

    override suspend fun getPrerequisitesBySkill(skillCode: SkillCode): List<SkillCode> {
        return emptyList()
    }
}
