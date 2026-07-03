package com.example.russianpath.data.repository

import com.example.russianpath.data.local.dao.TopicDao
import com.example.russianpath.data.local.entity.TopicEntity
import com.example.russianpath.domain.model.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с темами обучения.
 *
 * Предоставляет методы для получения списка тем
 * с преобразованием из Entity в доменную модель.
 */
@Singleton
class TopicRepository @Inject constructor(
    private val topicDao: TopicDao
) {

    /**
     * Возвращает активные темы по ID класса.
     * Flow для реактивного обновления UI.
     *
     * @param gradeId ID класса (например, "5", "11", "oge", "ege").
     */
    fun observeTopicsByGrade(gradeId: String): Flow<List<Topic>> {
        return topicDao.observeByGrade(gradeId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Возвращает все темы (для карты контента).
     */
    fun observeAllTopics(): Flow<List<Topic>> {
        return topicDao.observeAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Возвращает активные темы по ID раздела.
     *
     * @param sectionId ID раздела.
     */
    fun observeTopicsBySection(sectionId: String): Flow<List<Topic>> {
        return topicDao.observeBySection(sectionId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Возвращает тему по ID (однократно).
     */
    suspend fun getTopicById(id: String): Topic? {
        return topicDao.getById(id)?.toDomainModel()
    }

    /**
     * Разблокирует тему.
     */
    suspend fun unlockTopic(id: String) {
        topicDao.unlockTopic(id)
    }

    /**
     * Проверяет, разблокирована ли тема.
     */
    suspend fun isTopicUnlocked(id: String): Boolean {
        return topicDao.isTopicUnlocked(id)
    }

    /**
     * Возвращает ID тем-пререквизитов для указанной темы.
     * Используется для проверки, можно ли открыть тему.
     */
    suspend fun getPrerequisiteTopicIds(id: String): List<String> {
        val json = topicDao.getPrerequisiteIds(id)
        return parseJsonStringList(json)
    }

    /**
     * Парсит JSON-строку в список строк.
     * Ожидает формат: ["id1", "id2", ...]
     */
    private fun parseJsonStringList(json: String): List<String> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            json.trim('[', ']')
                .split(",")
                .map { it.trim().trim('"') }
                .filter { it.isNotBlank() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Преобразует TopicEntity в доменную модель Topic.
     */
    private fun TopicEntity.toDomainModel(): Topic {
        return Topic(
            id = id,
            sectionId = sectionId,
            gradeId = gradeId,
            title = title,
            description = description,
            iconName = iconName,
            sortOrder = sortOrder,
            isUnlocked = isUnlocked,
            difficultyLevel = difficultyLevel,
            estimatedMinutes = estimatedMinutes,
            prerequisiteTopicIds = parseJsonStringList(prerequisiteTopicIdsJson),
            completionPercentage = 0f,
            stars = 0
        )
    }
}
