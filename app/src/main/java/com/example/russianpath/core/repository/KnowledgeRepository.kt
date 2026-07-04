// app/src/main/java/com/example/russianpath/core/repository/KnowledgeRepository.kt

package com.example.russianpath.core.repository

import com.example.russianpath.data.local.converter.SkillCode

/**
 * Репозиторий для работы с образовательным графом знаний.
 *
 * Предоставляет методы для:
 * - Получения целей обучения
 * - Получения микро-навыков
 * - Построения графа пререквизитов (навыки, которые нужно освоить перед целевым)
 * - Аналитики освоения навыков
 *
 * Реализация использует LearningObjectiveDao, MicroSkillDao и TopicDao.
 */
interface KnowledgeRepository {

    /**
     * Возвращает цель обучения по её ID.
     *
     * @param objectiveId ID цели обучения.
     * @return LearningObjective или null, если цель не найдена.
     */
    suspend fun getObjectiveById(objectiveId: String): LearningObjective?

    /**
     * Возвращает список микро-навыков для указанной цели обучения.
     *
     * @param objectiveId ID цели обучения.
     * @return Список микро-навыков, отсортированный по порядку.
     */
    suspend fun getMicroSkillsByObjective(objectiveId: String): List<MicroSkill>

    /**
     * Возвращает список кодов навыков, которые являются пререквизитами
     * для указанного кода навыка.
     *
     * Используется для построения графа зависимостей:
     * "чтобы освоить синтаксический анализ, нужно сначала освоить морфологический".
     *
     * @param skillCode Код навыка, для которого ищем пререквизиты.
     * @return Список кодов навыков-пререквизитов.
     */
    suspend fun getPrerequisitesBySkill(skillCode: SkillCode): List<SkillCode>

    /**
     * Возвращает все цели обучения для указанной темы.
     *
     * @param topicId ID темы.
     * @return Список целей обучения.
     */
    suspend fun getObjectivesByTopic(topicId: String): List<LearningObjective>

    /**
     * Возвращает цели обучения по коду навыка (из кодификатора ОГЭ/ЕГЭ).
     *
     * @param skillCode Код навыка.
     * @return Список целей обучения, связанных с этим кодом.
     */
    suspend fun getObjectivesBySkillCode(skillCode: SkillCode): List<LearningObjective>

    /**
     * Возвращает корневые микро-навыки для цели обучения
     * (навыки, не имеющие родительских).
     *
     * @param objectiveId ID цели обучения.
     * @return Список корневых микро-навыков.
     */
    suspend fun getRootMicroSkillsByObjective(objectiveId: String): List<MicroSkill>

    /**
     * Возвращает дочерние микро-навыки для указанного родительского.
     *
     * @param parentSkillId ID родительского микро-навыка.
     * @return Список дочерних микро-навыков.
     */
    suspend fun getChildMicroSkills(parentSkillId: String): List<MicroSkill>

    /**
     * Возвращает микро-навыки по категории ошибок.
     * Используется для аналитики: "все навыки, где пользователь допускает фонетические ошибки".
     *
     * @param errorCategory Категория ошибок (например, "phonetic", "morphemic").
     * @return Список микро-навыков в этой категории.
     */
    suspend fun getMicroSkillsByErrorCategory(errorCategory: String): List<MicroSkill>

    /**
     * Возвращает типичные ошибочные паттерны для микро-навыка.
     *
     * @param skillId ID микро-навыка.
     * @return Список строк с описанием типичных ошибок.
     */
    suspend fun getMistakePatterns(skillId: String): List<String>
}

// ========================================================================
// Доменные модели
// ========================================================================

/**
 * Доменная модель цели обучения.
 */
data class LearningObjective(
    val id: String,
    val topicId: String,
    val skillCodeId: Int,
    val name: String,
    val description: String,
    val sortOrder: Int,
    val prerequisiteObjectiveIds: List<String>,
    val bloomTaxonomyLevel: Int,
    val masteryThresholdPercent: Int,
    val isRequired: Boolean
)

/**
 * Доменная модель микро-навыка.
 */
data class MicroSkill(
    val id: String,
    val objectiveId: String,
    val skillCodeId: Int,
    val parentMicroSkillId: String,
    val name: String,
    val description: String,
    val sortOrder: Int,
    val difficultyLevel: Int,
    val errorCategory: String,
    val typicalMistakePatterns: List<String>
)
