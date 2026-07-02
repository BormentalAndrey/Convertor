package com.example.russianpath.data.repository

import com.example.russianpath.core.knowledge.*
import com.example.russianpath.core.repository.KnowledgeRepository
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.mapper.KnowledgeMapper
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnowledgeRepositoryImpl @Inject constructor(
    private val objectiveDao: LearningObjectiveDao,
    private val skillDao: MicroSkillDao,
    private val mapper: KnowledgeMapper
) : KnowledgeRepository {

    override suspend fun getObjectiveById(id: ObjectiveId): LearningObjective {
        val entity = objectiveDao.getById(id.value)
            ?: throw NoSuchElementException("Objective not found: $id")
        return mapper.toDomain(entity)
    }

    override suspend fun getMicroSkillsByObjective(objectiveId: ObjectiveId): List<MicroSkill> {
        return skillDao.observeByObjective(objectiveId.value)
            .firstOrNull()
            ?.map { mapper.toDomain(it) }
            ?: emptyList()
    }

    override suspend fun getPrerequisitesBySkill(skillCode: SkillCode): List<SkillCode> {
        // В v1.0 — возвращаем пустой список.
        // В v2.0 — поиск по таблице связей skill_prerequisites.
        return emptyList()
    }
}
