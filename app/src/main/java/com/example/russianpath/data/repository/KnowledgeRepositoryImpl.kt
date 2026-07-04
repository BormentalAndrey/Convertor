// app/src/main/java/com/example/russianpath/data/repository/KnowledgeRepositoryImpl.kt

package com.example.russianpath.data.repository

import com.example.russianpath.core.repository.KnowledgeRepository
import com.example.russianpath.core.repository.LearningObjective
import com.example.russianpath.core.repository.MicroSkill
import com.example.russianpath.data.local.converter.SkillCode
import com.example.russianpath.data.local.dao.LearningObjectiveDao
import com.example.russianpath.data.local.dao.MicroSkillDao
import com.example.russianpath.data.local.mapper.KnowledgeMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация KnowledgeRepository.
 *
 * Работает с графом образовательных знаний:
 * - Цели обучения (LearningObjective)
 * - Микро-навыки (MicroSkill)
 * - Коды навыков из кодификатора (SkillCode)
 *
 * Все методы выполняются на IO-диспетчере для безопасности основного потока.
 * Маппинг Entity → Domain вынесен в KnowledgeMapper для соблюдения Single Responsibility.
 *
 * @see KnowledgeRepository
 * @see KnowledgeMapper
 */
@Singleton
class KnowledgeRepositoryImpl @Inject constructor(
    private val learningObjectiveDao: LearningObjectiveDao,
    private val microSkillDao: MicroSkillDao,
    private val knowledgeMapper: KnowledgeMapper
) : KnowledgeRepository {

    // ========================================================================
    // Цели обучения (LearningObjective)
    // ========================================================================

    override suspend fun getObjectiveById(objectiveId: String): LearningObjective? {
        return withContext(Dispatchers.IO) {
            val entity = learningObjectiveDao.getById(objectiveId)
            entity?.let { knowledgeMapper.toDomain(it) }
        }
    }

    override suspend fun getObjectivesByTopic(topicId: String): List<LearningObjective> {
        return withContext(Dispatchers.IO) {
            val entities = learningObjectiveDao.getAllByTopic(topicId)
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    override suspend fun getObjectivesBySkillCode(skillCode: SkillCode): List<LearningObjective> {
        return withContext(Dispatchers.IO) {
            val entities = learningObjectiveDao.observeBySkillCode(skillCode.code).first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    // ========================================================================
    // Микро-навыки (MicroSkill)
    // ========================================================================

    override suspend fun getMicroSkillsByObjective(objectiveId: String): List<MicroSkill> {
        return withContext(Dispatchers.IO) {
            val entities = microSkillDao.observeByObjective(objectiveId).first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    override suspend fun getRootMicroSkillsByObjective(objectiveId: String): List<MicroSkill> {
        return withContext(Dispatchers.IO) {
            val entities = microSkillDao.observeRootsByObjective(objectiveId).first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    override suspend fun getChildMicroSkills(parentSkillId: String): List<MicroSkill> {
        return withContext(Dispatchers.IO) {
            val entities = microSkillDao.observeByParent(parentSkillId).first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    override suspend fun getMicroSkillsByErrorCategory(
        errorCategory: String
    ): List<MicroSkill> {
        return withContext(Dispatchers.IO) {
            val entities = microSkillDao.observeByErrorCategory(errorCategory).first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    override suspend fun getMistakePatterns(skillId: String): List<String> {
        return withContext(Dispatchers.IO) {
            val json = microSkillDao.getMistakePatterns(skillId)
            knowledgeMapper.parseStringListFromJson(json)
        }
    }

    // ========================================================================
    // Граф пререквизитов
    // ========================================================================

    override suspend fun getPrerequisitesBySkill(skillCode: SkillCode): List<SkillCode> {
        return withContext(Dispatchers.IO) {
            val prerequisites = mutableSetOf<SkillCode>()

            val objectives = learningObjectiveDao
                .observeBySkillCode(skillCode.code)
                .first()

            for (objective in objectives) {
                val prerequisiteIdsJson = learningObjectiveDao
                    .getPrerequisiteIds(objective.id)
                val prerequisiteIds = knowledgeMapper.parseStringListFromJson(prerequisiteIdsJson)

                for (prereqId in prerequisiteIds) {
                    val prereqObjective = learningObjectiveDao.getById(prereqId)
                    if (prereqObjective != null && prereqObjective.skillCodeId > 0) {
                        SkillCode.entries
                            .firstOrNull { it.code == prereqObjective.skillCodeId }
                            ?.let { prerequisites.add(it) }
                    }
                }
            }

            if (prerequisites.isEmpty()) {
                prerequisites.addAll(getImplicitPrerequisites(skillCode))
            }

            prerequisites.toList()
        }
    }

    private fun getImplicitPrerequisites(skillCode: SkillCode): List<SkillCode> {
        return when (skillCode) {
            SkillCode.MORPHEMIC_ANALYSIS -> listOf(SkillCode.PHONETIC_ANALYSIS)
            SkillCode.MORPHOLOGICAL_ANALYSIS -> listOf(
                SkillCode.PHONETIC_ANALYSIS,
                SkillCode.MORPHEMIC_ANALYSIS
            )
            SkillCode.SYNTACTIC_ANALYSIS -> listOf(
                SkillCode.PHONETIC_ANALYSIS,
                SkillCode.MORPHEMIC_ANALYSIS,
                SkillCode.MORPHOLOGICAL_ANALYSIS
            )
            SkillCode.TEXT_ANALYSIS -> listOf(
                SkillCode.SYNTACTIC_ANALYSIS,
                SkillCode.MORPHOLOGICAL_ANALYSIS
            )
            SkillCode.ORTHOGRAPHY_NORMS -> listOf(SkillCode.ORTHOGRAPHIC_ANALYSIS)
            SkillCode.PUNCTUATION_NORMS -> listOf(
                SkillCode.PUNCTUATION_ANALYSIS,
                SkillCode.SYNTACTIC_ANALYSIS
            )
            SkillCode.GRAMMAR_NORMS -> listOf(
                SkillCode.MORPHOLOGICAL_ANALYSIS,
                SkillCode.SYNTACTIC_ANALYSIS
            )
            SkillCode.SPEECH_NORMS -> listOf(
                SkillCode.LEXICAL_MEANS,
                SkillCode.GRAMMAR_NORMS,
                SkillCode.SPEECH_STYLES
            )
            SkillCode.SPEECH_TYPES -> listOf(SkillCode.SPEECH_STYLES)
            SkillCode.TEXT_CREATION -> listOf(
                SkillCode.TEXT_ANALYSIS,
                SkillCode.GRAMMAR_NORMS,
                SkillCode.SPEECH_NORMS,
                SkillCode.ORTHOGRAPHY_NORMS,
                SkillCode.PUNCTUATION_NORMS
            )
            SkillCode.TEXT_EDITING -> listOf(
                SkillCode.TEXT_CREATION,
                SkillCode.TEXT_ANALYSIS,
                SkillCode.ORTHOGRAPHY_NORMS,
                SkillCode.PUNCTUATION_NORMS
            )
            SkillCode.EXPRESSIVE_MEANS -> listOf(
                SkillCode.LEXICAL_MEANS,
                SkillCode.SPEECH_STYLES
            )
            SkillCode.INFORMATION_PROCESSING -> listOf(
                SkillCode.TEXT_ANALYSIS,
                SkillCode.SPEECH_STYLES,
                SkillCode.SPEECH_TYPES
            )
            else -> emptyList()
        }
    }
}
