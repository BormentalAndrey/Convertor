package com.example.russianpath.data.repository

import com.example.russianpath.core.knowledge.LearningObjective
import com.example.russianpath.core.knowledge.MicroSkill
import com.example.russianpath.core.knowledge.SkillCode
import com.example.russianpath.core.repository.KnowledgeRepository
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.mapper.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnowledgeRepositoryImpl @Inject constructor(
    private val learningObjectiveDao: LearningObjectiveDao,
    private val microSkillDao: MicroSkillDao
) : KnowledgeRepository {

    override suspend fun getObjectiveById(objectiveId: String): LearningObjective {
        val entity = learningObjectiveDao.getById(objectiveId)
            ?: throw IllegalArgumentException("Objective not found: $objectiveId")
        return entity.toDomain()
    }

    override suspend fun getMicroSkillsByObjective(objectiveId: String): List<MicroSkill> {
        // Для Flow используем first() чтобы получить текущее значение
        return kotlinx.coroutines.flow.first { true }
            .let { microSkillDao.getByObjective(objectiveId) }
            .let { flow ->
                // Flow → List: используем first()
                var result: List<MicroSkill> = emptyList()
                kotlinx.coroutines.runBlocking {
                    result = flow.first().map { it.toDomain() }
                }
                result
            }
    }

    override suspend fun getPrerequisitesBySkill(skillCode: SkillCode): List<SkillCode> {
        // Упрощение: пока возвращаем пустой список
        return emptyList()
    }
}
