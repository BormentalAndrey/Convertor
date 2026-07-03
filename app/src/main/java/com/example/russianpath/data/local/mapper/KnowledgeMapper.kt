package com.example.russianpath.data.local.mapper

import com.example.russianpath.core.knowledge.LearningObjective
import com.example.russianpath.core.knowledge.MicroSkill
import com.example.russianpath.data.local.entity.LearningObjectiveEntity
import com.example.russianpath.data.local.entity.MicroSkillEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Маппер для преобразования Entity → Domain моделей знаний.
 *
 * Отвечает за:
 * - LearningObjectiveEntity → LearningObjective
 * - MicroSkillEntity → MicroSkill
 * - Парсинг JSON-полей в коллекции
 *
 * Вынесен в отдельный класс для соблюдения Single Responsibility:
 * Repository не должен знать о деталях маппинга.
 */
@Singleton
class KnowledgeMapper @Inject constructor() {

    private val gson = Gson()

    // ========================================================================
    // Маппинг LearningObjective
    // ========================================================================

    /**
     * Преобразует LearningObjectiveEntity в доменную модель LearningObjective.
     *
     * @param entity Сущность из БД.
     * @return Доменная модель.
     */
    fun toDomain(entity: LearningObjectiveEntity): LearningObjective {
        return LearningObjective(
            id = entity.id,
            topicId = entity.topicId,
            skillCodeId = entity.skillCodeId,
            name = entity.name,
            description = entity.description,
            sortOrder = entity.sortOrder,
            prerequisiteObjectiveIds = parseStringListFromJson(entity.prerequisiteObjectiveIdsJson),
            bloomTaxonomyLevel = entity.bloomTaxonomyLevel,
            masteryThresholdPercent = entity.masteryThresholdPercent,
            isRequired = entity.isRequired
        )
    }

    /**
     * Пакетное преобразование списка целей обучения.
     */
    fun toDomainList(entities: List<LearningObjectiveEntity>): List<LearningObjective> {
        return entities.map { toDomain(it) }
    }

    // ========================================================================
    // Маппинг MicroSkill
    // ========================================================================

    /**
     * Преобразует MicroSkillEntity в доменную модель MicroSkill.
     *
     * @param entity Сущность из БД.
     * @return Доменная модель.
     */
    fun toDomain(entity: MicroSkillEntity): MicroSkill {
        return MicroSkill(
            id = entity.id,
            objectiveId = entity.objectiveId,
            skillCodeId = entity.skillCodeId,
            parentMicroSkillId = entity.parentMicroSkillId,
            name = entity.name,
            description = entity.description,
            sortOrder = entity.sortOrder,
            difficultyLevel = entity.difficultyLevel,
            errorCategory = entity.errorCategory,
            typicalMistakePatterns = parseStringListFromJson(entity.typicalMistakePatternJson)
        )
    }

    /**
     * Пакетное преобразование списка микро-навыков.
     */
    fun toDomainList(entities: List<MicroSkillEntity>): List<MicroSkill> {
        return entities.map { toDomain(it) }
    }

    // ========================================================================
    // Парсинг JSON
    // ========================================================================

    /**
     * Безопасно парсит JSON-строку в список строк.
     *
     * При ошибке парсинга или пустой строке возвращает emptyList().
     * Никогда не выбрасывает исключений.
     *
     * @param json JSON-строка в формате ["str1", "str2", ...]
     * @return Список строк.
     */
    fun parseStringListFromJson(json: String): List<String> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Безопасно парсит JSON-строку в список целых чисел.
     *
     * @param json JSON-строка в формате [1, 2, 3]
     * @return Список чисел.
     */
    fun parseIntListFromJson(json: String): List<Int> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Безопасно парсит JSON-строку в Map.
     *
     * @param json JSON-строка в формате {"key": "value", ...}
     * @return Map.
     */
    fun parseMapFromJson(json: String): Map<String, Any> {
        if (json.isBlank() || json == "{}") return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
