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

    /**
     * Возвращает цель обучения по её ID.
     *
     * Выполняется на IO-диспетчере.
     * Если цель не найдена — выбрасывает NoSuchElementException с информативным сообщением.
     *
     * @param id ID цели обучения.
     * @return Доменная модель LearningObjective.
     * @throws NoSuchElementException если цель с указанным ID не существует.
     */
    override suspend fun getObjectiveById(id: ObjectiveId): LearningObjective {
        return withContext(Dispatchers.IO) {
            val entity = learningObjectiveDao.getById(id.value)
                ?: throw NoSuchElementException(
                    "LearningObjective not found for id: ${id.value}. " +
                            "Ensure the objective exists in the database and the ID is correct."
                )
            knowledgeMapper.toDomain(entity)
        }
    }

    /**
     * Безопасный вариант получения цели обучения.
     * Возвращает null, если цель не найдена.
     *
     * @param id ID цели обучения.
     * @return LearningObjective или null.
     */
    suspend fun getObjectiveByIdOrNull(id: ObjectiveId): LearningObjective? {
        return withContext(Dispatchers.IO) {
            val entity = learningObjectiveDao.getById(id.value)
            entity?.let { knowledgeMapper.toDomain(it) }
        }
    }

    /**
     * Возвращает все цели обучения для указанной темы.
     *
     * @param topicId ID темы в виде строки.
     * @return Список целей обучения, отсортированный по sortOrder.
     */
    suspend fun getObjectivesByTopic(topicId: String): List<LearningObjective> {
        return withContext(Dispatchers.IO) {
            val entities = learningObjectiveDao.getAllByTopic(topicId)
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    /**
     * Возвращает цели обучения, связанные с указанным кодом навыка.
     *
     * @param skillCode Код навыка из кодификатора ОГЭ/ЕГЭ.
     * @return Список целей обучения.
     */
    suspend fun getObjectivesBySkillCode(skillCode: SkillCode): List<LearningObjective> {
        return withContext(Dispatchers.IO) {
            val entities = learningObjectiveDao.observeBySkillCode(skillCode.code).first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    // ========================================================================
    // Микро-навыки (MicroSkill)
    // ========================================================================

    /**
     * Возвращает микро-навыки для указанной цели обучения.
     *
     * @param objectiveId ID цели обучения.
     * @return Список микро-навыков, отсортированный по sortOrder.
     */
    override suspend fun getMicroSkillsByObjective(objectiveId: ObjectiveId): List<MicroSkill> {
        return withContext(Dispatchers.IO) {
            val entities = microSkillDao
                .observeByObjective(objectiveId.value)
                .first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    /**
     * Возвращает микро-навык по его ID.
     *
     * @param id ID микро-навыка.
     * @return MicroSkill или null, если не найден.
     */
    suspend fun getMicroSkillById(id: MicroSkillId): MicroSkill? {
        return withContext(Dispatchers.IO) {
            val entity = microSkillDao.getById(id.value)
            entity?.let { knowledgeMapper.toDomain(it) }
        }
    }

    /**
     * Возвращает корневые микро-навыки (без родителя) для цели обучения.
     *
     * @param objectiveId ID цели обучения.
     * @return Список корневых микро-навыков.
     */
    suspend fun getRootMicroSkillsByObjective(objectiveId: ObjectiveId): List<MicroSkill> {
        return withContext(Dispatchers.IO) {
            val entities = microSkillDao.observeRootsByObjective(objectiveId.value).first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    /**
     * Возвращает дочерние микро-навыки для указанного родительского.
     *
     * @param parentId ID родительского микро-навыка.
     * @return Список дочерних микро-навыков.
     */
    suspend fun getChildMicroSkills(parentId: MicroSkillId): List<MicroSkill> {
        return withContext(Dispatchers.IO) {
            val entities = microSkillDao.observeByParent(parentId.value).first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    /**
     * Возвращает микро-навыки, сгруппированные по категории ошибок.
     * Используется для аналитики: "все навыки с фонетическими ошибками".
     *
     * @param errorCategory Категория ошибки (например, "phonetic", "morphemic", "syntactic").
     * @return Список микро-навыков этой категории.
     */
    suspend fun getMicroSkillsByErrorCategory(errorCategory: String): List<MicroSkill> {
        return withContext(Dispatchers.IO) {
            val entities = microSkillDao.observeByErrorCategory(errorCategory).first()
            entities.map { knowledgeMapper.toDomain(it) }
        }
    }

    /**
     * Возвращает типичные ошибочные паттерны для микро-навыка.
     *
     * @param skillId ID микро-навыка.
     * @return Список строк с описанием типичных ошибок.
     */
    suspend fun getMistakePatterns(skillId: MicroSkillId): List<String> {
        return withContext(Dispatchers.IO) {
            val json = microSkillDao.getMistakePatterns(skillId.value)
            knowledgeMapper.parseStringListFromJson(json)
        }
    }

    // ========================================================================
    // Граф пререквизитов (Prerequisites)
    // ========================================================================

    /**
     * Возвращает коды навыков, которые являются пререквизитами
     * для указанного кода навыка.
     *
     * Алгоритм:
     * 1. Находит все цели обучения с указанным skillCode.
     * 2. Для каждой цели получает явные пререквизиты из prerequisiteObjectiveIdsJson.
     * 3. Для каждого пререквизита получает его skillCodeId и преобразует в SkillCode.
     * 4. Если явных пререквизитов нет — определяет неявные по иерархии кодов.
     *
     * Пререквизиты дедуплицируются через Set.
     *
     * @param skillCode Код навыка, для которого ищем пререквизиты.
     * @return Список кодов навыков-пререквизитов (может быть пустым).
     */
    override suspend fun getPrerequisitesBySkill(skillCode: SkillCode): List<SkillCode> {
        return withContext(Dispatchers.IO) {
            val prerequisites = mutableSetOf<SkillCode>()

            // Шаг 1: Находим все цели обучения с этим кодом навыка
            val objectives = learningObjectiveDao
                .observeBySkillCode(skillCode.code)
                .first()

            // Шаг 2-3: Извлекаем явные пререквизиты
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

            // Шаг 4: Если явных пререквизитов нет — используем неявные
            if (prerequisites.isEmpty()) {
                val implicitPrerequisites = getImplicitPrerequisites(skillCode)
                prerequisites.addAll(implicitPrerequisites)
            }

            prerequisites.toList()
        }
    }

    /**
     * Определяет неявные пререквизиты на основе иерархии кодов навыков.
     *
     * Иерархия построена на педагогической логике изучения русского языка:
     * от простого к сложному, от анализа к синтезу.
     *
     * Примеры:
     * - Морфемный анализ требует фонетического.
     * - Синтаксический анализ требует морфологического.
     * - Орфографические нормы требуют орфографического анализа.
     * - Создание текста требует анализа текста, грамматических и речевых норм.
     *
     * @param skillCode Код навыка.
     * @return Список кодов навыков-пререквизитов.
     */
    private fun getImplicitPrerequisites(skillCode: SkillCode): List<SkillCode> {
        return when (skillCode) {
            // Анализ языка: от звука к тексту
            SkillCode.MORPHEMIC_ANALYSIS -> listOf(
                SkillCode.PHONETIC_ANALYSIS
            )
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

            // Нормы: анализ предшествует соблюдению норм
            SkillCode.ORTHOGRAPHY_NORMS -> listOf(
                SkillCode.ORTHOGRAPHIC_ANALYSIS
            )
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

            // Речь: от распознавания к порождению
            SkillCode.SPEECH_TYPES -> listOf(
                SkillCode.SPEECH_STYLES
            )
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

            // Лексика и выразительность
            SkillCode.EXPRESSIVE_MEANS -> listOf(
                SkillCode.LEXICAL_MEANS,
                SkillCode.SPEECH_STYLES
            )

            // Информационная обработка — мета-навык
            SkillCode.INFORMATION_PROCESSING -> listOf(
                SkillCode.TEXT_ANALYSIS,
                SkillCode.SPEECH_STYLES,
                SkillCode.SPEECH_TYPES
            )

            // Начальные навыки пререквизитов не имеют
            SkillCode.IDENTIFY_LANGUAGE_UNITS,
            SkillCode.PHONETIC_ANALYSIS,
            SkillCode.ORTHOGRAPHIC_ANALYSIS,
            SkillCode.PUNCTUATION_ANALYSIS,
            SkillCode.LEXICAL_MEANS,
            SkillCode.SPEECH_STYLES,
            SkillCode.UNKNOWN -> emptyList()
        }
    }

    // ========================================================================
    // Комплексные запросы
    // ========================================================================

    /**
     * Проверяет, освоен ли микро-навык и все его пререквизиты.
     *
     * @param skillId ID микро-навыка.
     * @param masteredSkillCodes Множество кодов навыков, уже освоенных пользователем.
     * @return true если навык и все его пререквизиты освоены.
     */
    suspend fun isSkillMasteredWithPrerequisites(
        skillId: MicroSkillId,
        masteredSkillCodes: Set<Int>
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val skill = microSkillDao.getById(skillId.value) ?: return@withContext false

            // Проверяем сам навык
            if (skill.skillCodeId !in masteredSkillCodes) return@withContext false

            // Находим SkillCode навыка
            val skillCode = SkillCode.entries.firstOrNull { it.code == skill.skillCodeId }
                ?: return@withContext false

            // Проверяем все пререквизиты
            val prerequisites = getPrerequisitesBySkill(skillCode)
            prerequisites.all { prereq -> prereq.code in masteredSkillCodes }
        }
    }

    /**
     * Строит полный граф зависимостей для списка кодов навыков.
     *
     * @param skillCodes Список кодов навыков, для которых строится граф.
     * @return Map: SkillCode → список его пререквизитов.
     */
    suspend fun buildPrerequisiteGraph(
        skillCodes: List<SkillCode>
    ): Map<SkillCode, List<SkillCode>> {
        return withContext(Dispatchers.IO) {
            val graph = mutableMapOf<SkillCode, List<SkillCode>>()
            for (skillCode in skillCodes) {
                graph[skillCode] = getPrerequisitesBySkill(skillCode)
            }
            graph
        }
    }

    /**
     * Рекурсивно собирает все транзитивные пререквизиты для навыка.
     *
     * Например, для TEXT_CREATION вернёт:
     * [TEXT_ANALYSIS, GRAMMAR_NORMS, SPEECH_NORMS, ORTHOGRAPHY_NORMS, PUNCTUATION_NORMS,
     *  SYNTACTIC_ANALYSIS, MORPHOLOGICAL_ANALYSIS, MORPHEMIC_ANALYSIS, PHONETIC_ANALYSIS, ...]
     *
     * @param skillCode Код навыка.
     * @return Множество всех навыков, которые нужно освоить перед целевым.
     */
    suspend fun getAllTransitivePrerequisites(skillCode: SkillCode): Set<SkillCode> {
        return withContext(Dispatchers.IO) {
            val allPrerequisites = mutableSetOf<SkillCode>()
            collectTransitivePrerequisites(skillCode, allPrerequisites)
            allPrerequisites
        }
    }

    /**
     * Рекурсивно собирает транзитивные пререквизиты.
     */
    private suspend fun collectTransitivePrerequisites(
        skillCode: SkillCode,
        accumulator: MutableSet<SkillCode>
    ) {
        val directPrerequisites = getPrerequisitesBySkill(skillCode)
        for (prereq in directPrerequisites) {
            if (accumulator.add(prereq)) {
                // Рекурсивно собираем пререквизиты пререквизитов
                collectTransitivePrerequisites(prereq, accumulator)
            }
        }
    }
}
