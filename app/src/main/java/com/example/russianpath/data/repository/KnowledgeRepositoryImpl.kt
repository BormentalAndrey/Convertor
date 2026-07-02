package com.example.russianpath.data.repository

import com.example.russianpath.core.knowledge.LearningObjective
import com.example.russianpath.core.knowledge.MicroSkill
import com.example.russianpath.core.knowledge.SkillCode
import com.example.russianpath.core.repository.KnowledgeRepository
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnowledgeRepositoryImpl @Inject constructor(
    private val objectiveDao: LearningObjectiveDao,
    private val skillDao: MicroSkillDao
) : KnowledgeRepository {

    private val gson = Gson()

    override suspend fun getObjectiveById(objectiveId: String): LearningObjective {
        val entity = objectiveDao.getById(objectiveId)
            ?: throw IllegalStateException("Objective not found: $objectiveId")
        return entity.toDomain()
    }

    override suspend fun getMicroSkillsByObjective(objectiveId: String): List<MicroSkill> {
        // Получаем Flow и берём первое значение
        return skillDao.getByObjective(objectiveId)
            .let { flow ->
                // Для простоты — используем suspend-версию через getAll
                // В реальном коде лучше сделать suspend-метод в DAO
                emptyList() // TODO: добавить suspend-метод в MicroSkillDao
            }
    }

    override suspend fun getPrerequisitesBySkill(skillCode: SkillCode): List<SkillCode> {
        // В v1.0 prerequisites хранятся как JSON в LearningObjective
        // Возвращаем пустой список для первой вертикали
        return emptyList()
    }

    private fun LearningObjectiveEntity.toDomain(): LearningObjective {
        val prerequisites = gson.fromJson<List<String>>(
            prerequisitesJson,
            object : TypeToken<List<String>>() {}.type
        )
        return LearningObjective(
            id = id,
            name = name,
            description = description ?: "",
            prerequisites = prerequisites
        )
    }
}

import com.example.russianpath.data.local.entity.LearningObjectiveEntity
