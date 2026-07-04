// app/src/main/java/com/example/russianpath/data/repository/LessonRepository.kt

package com.example.russianpath.data.repository

import com.example.russianpath.data.local.dao.LessonCompletionDao
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.entity.LessonEntity
import com.example.russianpath.data.local.entity.QuestionEntity
import com.example.russianpath.domain.model.Lesson
import com.example.russianpath.domain.model.LessonType
import com.example.russianpath.domain.model.Question
import com.example.russianpath.domain.model.QuestionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с уроками и вопросами.
 *
 * Предоставляет методы для:
 * - Получения уроков по теме
 * - Получения вопросов урока
 * - Получения истории прохождений
 * - Получения уроков для повторения (с ошибками)
 *
 * Все методы потокобезопасны (Room + Coroutines).
 */
@Singleton
class LessonRepository @Inject constructor(
    private val lessonDao: LessonDao,
    private val questionDao: QuestionDao,
    private val lessonCompletionDao: LessonCompletionDao
) {

    private val gson = Gson()

    // ========================================================================
    // Уроки (Lesson)
    // ========================================================================

    fun observeLessonsByTopic(topicId: String): Flow<List<Lesson>> {
        return lessonDao.observeByTopic(topicId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getLessonById(lessonId: String): Lesson? {
        return withContext(Dispatchers.IO) {
            lessonDao.getById(lessonId)?.toDomainModel()
        }
    }

    fun observeLessonsByTopicAndType(
        topicId: String,
        lessonType: LessonType
    ): Flow<List<Lesson>> {
        return lessonDao.observeByTopicAndType(topicId, lessonType.name.lowercase())
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    fun observeDiagnosticLessonsByTopic(topicId: String): Flow<List<Lesson>> {
        return lessonDao.observeDiagnosticByTopic(topicId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun observeBonusLessonsByTopic(topicId: String): Flow<List<Lesson>> {
        return lessonDao.observeBonusByTopic(topicId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getLessonsWithMistakes(limit: Int = 10): List<Lesson> {
        return withContext(Dispatchers.IO) {
            lessonDao.getLessonsWithMistakes(limit).map { it.toDomainModel() }
        }
    }

    suspend fun getLessonCountByTopic(topicId: String): Int {
        return withContext(Dispatchers.IO) {
            lessonDao.countByTopic(topicId)
        }
    }

    suspend fun enrichWithProgress(lesson: Lesson): Lesson {
        return withContext(Dispatchers.IO) {
            val latestCompletion = lessonCompletionDao.getLatestCompletion(lesson.id)
            val bestCompletion = lessonCompletionDao.getBestCompletion(lesson.id)
            val attemptCount = lessonCompletionDao.getAttemptCount(lesson.id)

            lesson.copy(
                isCompleted = latestCompletion != null && latestCompletion.isPassed,
                bestStars = bestCompletion?.stars ?: 0,
                bestScorePercent = bestCompletion?.scorePercent ?: 0,
                attemptCount = attemptCount
            )
        }
    }

    suspend fun enrichWithProgress(lessons: List<Lesson>): List<Lesson> {
        return withContext(Dispatchers.IO) {
            lessons.map { enrichWithProgress(it) }
        }
    }

    // ========================================================================
    // Вопросы (Question)
    // ========================================================================

    fun observeQuestionsByLesson(lessonId: String): Flow<List<Question>> {
        return questionDao.observeByLessonOrdered(lessonId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun observeQuestionsByLessonRandom(lessonId: String): Flow<List<Question>> {
        return questionDao.observeByLessonRandom(lessonId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getQuestionById(questionId: String): Question? {
        return withContext(Dispatchers.IO) {
            questionDao.getById(questionId)?.toDomainModel()
        }
    }

    fun observeQuestionsBySkill(skillId: String): Flow<List<Question>> {
        return questionDao.observeBySkill(skillId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getQuestionsByDifficultyRange(
        minDifficulty: Int,
        maxDifficulty: Int,
        limit: Int = 20
    ): List<Question> {
        return withContext(Dispatchers.IO) {
            questionDao.getByDifficultyRange(minDifficulty, maxDifficulty, limit)
                .map { it.toDomainModel() }
        }
    }

    suspend fun getQuestionCountByLesson(lessonId: String): Int {
        return withContext(Dispatchers.IO) {
            questionDao.countByLesson(lessonId)
        }
    }

    // ========================================================================
    // Комплексные запросы
    // ========================================================================

    suspend fun getLessonWithQuestions(lessonId: String): LessonWithQuestions? {
        return withContext(Dispatchers.IO) {
            val lessonEntity = lessonDao.getById(lessonId) ?: return@withContext null
            val lesson = lessonEntity.toDomainModel()
            val enrichedLesson = enrichWithProgress(lesson)

            val questions = questionDao.getAllByLesson(lessonId)
                .map { it.toDomainModel() }

            LessonWithQuestions(
                lesson = enrichedLesson,
                questions = questions
            )
        }
    }

    suspend fun getLessonsWithQuestionsByTopic(topicId: String): List<LessonWithQuestions> {
        return withContext(Dispatchers.IO) {
            val lessonEntities = lessonDao.getAllByTopic(topicId)
            lessonEntities.map { entity ->
                val lesson = entity.toDomainModel()
                val enrichedLesson = enrichWithProgress(lesson)
                val questions = questionDao.getAllByLesson(entity.id)
                    .map { it.toDomainModel() }

                LessonWithQuestions(
                    lesson = enrichedLesson,
                    questions = questions
                )
            }
        }
    }

    suspend fun getLessonsForSpacedRepetition(limit: Int = 5): List<LessonWithQuestions> {
        return withContext(Dispatchers.IO) {
            val lessonsWithMistakes = lessonDao.getLessonsWithMistakes(limit)
            lessonsWithMistakes.map { entity ->
                val lesson = entity.toDomainModel()
                val enrichedLesson = enrichWithProgress(lesson)
                val questions = questionDao.getAllByLesson(entity.id)
                    .map { it.toDomainModel() }

                LessonWithQuestions(
                    lesson = enrichedLesson,
                    questions = questions
                )
            }
        }
    }

    // ========================================================================
    // Маппинг Entity → Domain
    // ========================================================================

    private fun LessonEntity.toDomainModel(): Lesson {
        return Lesson(
            id = id,
            topicId = topicId,
            primaryObjectiveId = primaryObjectiveId,
            lessonType = LessonType.fromString(lessonType),
            title = title,
            description = description,
            instructionText = instructionText,
            difficulty = difficulty,
            sortOrder = sortOrder,
            theoryJson = theoryJson,
            questionsCount = questionsCount,
            timeLimitSeconds = timeLimitSeconds,
            passingScorePercent = passingScorePercent,
            maxStars = maxStars,
            xpBaseReward = xpBaseReward,
            xpPerfectBonus = xpPerfectBonus,
            gemsReward = gemsReward,
            isBonus = isBonus,
            isDiagnostic = isDiagnostic
        )
    }

    private fun QuestionEntity.toDomainModel(): Question {
        val type = QuestionType.fromString(questionType)

        return when (type) {
            QuestionType.SINGLE_CHOICE -> {
                val options: List<String> = parseOptionsFromDataJson(dataJson)
                Question(
                    id = id,
                    lessonId = lessonId,
                    primarySkillId = primarySkillId,
                    questionType = type,
                    promptText = promptText,
                    promptAudioPath = promptAudioPath,
                    promptImagePath = promptImagePath,
                    options = options,
                    correctAnswer = correctAnswerJson,
                    acceptableAnswers = parseStringListFromJson(acceptableAnswersJson),
                    hintText = hintText,
                    explanationText = explanationText,
                    audioPath = audioPath,
                    ruleReference = ruleReference,
                    ruleReferenceId = ruleReferenceId,
                    difficulty = difficulty,
                    timeLimitSeconds = timeLimitSeconds,
                    points = points,
                    penaltyPoints = penaltyPoints,
                    maxAttempts = maxAttempts,
                    isRequired = isRequired
                )
            }
            QuestionType.MULTIPLE_CHOICE -> {
                val options: List<String> = parseOptionsFromDataJson(dataJson)
                Question(
                    id = id,
                    lessonId = lessonId,
                    primarySkillId = primarySkillId,
                    questionType = type,
                    promptText = promptText,
                    options = options,
                    correctAnswer = correctAnswerJson,
                    acceptableAnswers = parseStringListFromJson(acceptableAnswersJson),
                    hintText = hintText,
                    explanationText = explanationText,
                    ruleReference = ruleReference,
                    ruleReferenceId = ruleReferenceId,
                    difficulty = difficulty,
                    points = points,
                    isRequired = isRequired
                )
            }
            QuestionType.TEXT_INPUT,
            QuestionType.FILL_IN_BLANK,
            QuestionType.DICTATION -> {
                Question(
                    id = id,
                    lessonId = lessonId,
                    primarySkillId = primarySkillId,
                    questionType = type,
                    promptText = promptText,
                    promptAudioPath = promptAudioPath,
                    correctAnswer = correctAnswerJson,
                    acceptableAnswers = parseStringListFromJson(acceptableAnswersJson),
                    hintText = hintText,
                    explanationText = explanationText,
                    audioPath = audioPath,
                    ruleReference = ruleReference,
                    ruleReferenceId = ruleReferenceId,
                    difficulty = difficulty,
                    points = points,
                    isRequired = isRequired
                )
            }
            QuestionType.WORD_DRAG,
            QuestionType.SEQUENCE_ORDER -> {
                val dragData = parseDragDataFromJson(dataJson)
                Question(
                    id = id,
                    lessonId = lessonId,
                    primarySkillId = primarySkillId,
                    questionType = type,
                    promptText = promptText,
                    draggableWords = dragData.first,
                    correctOrder = dragData.second,
                    correctAnswer = correctAnswerJson,
                    hintText = hintText,
                    explanationText = explanationText,
                    ruleReference = ruleReference,
                    ruleReferenceId = ruleReferenceId,
                    difficulty = difficulty,
                    points = points,
                    isRequired = isRequired
                )
            }
            QuestionType.MATCHING -> {
                val pairs = parseMatchingPairsFromJson(dataJson)
                Question(
                    id = id,
                    lessonId = lessonId,
                    primarySkillId = primarySkillId,
                    questionType = type,
                    promptText = promptText,
                    options = pairs.first,
                    correctAnswer = correctAnswerJson,
                    hintText = hintText,
                    explanationText = explanationText,
                    ruleReference = ruleReference,
                    difficulty = difficulty,
                    points = points,
                    isRequired = isRequired
                )
            }
            QuestionType.STRESS_SELECTION,
            QuestionType.MORPHEMIC_ANALYSIS -> {
                Question(
                    id = id,
                    lessonId = lessonId,
                    primarySkillId = primarySkillId,
                    questionType = type,
                    promptText = promptText,
                    correctAnswer = correctAnswerJson,
                    hintText = hintText,
                    explanationText = explanationText,
                    ruleReference = ruleReference,
                    difficulty = difficulty,
                    points = points,
                    isRequired = isRequired
                )
            }
        }
    }

    // ========================================================================
    // Парсинг JSON
    // ========================================================================

    private fun parseOptionsFromDataJson(json: String): List<String> {
        return try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseDragDataFromJson(json: String): Pair<List<String>, List<Int>> {
        return try {
            val map: Map<String, Any> = gson.fromJson(
                json,
                object : TypeToken<Map<String, Any>>() {}.type
            )
            val words = (map["words"] as? List<*>)?.map { it.toString() } ?: emptyList()
            val order = (map["correct_order"] as? List<*>)
                ?.map { (it as? Double)?.toInt() ?: 0 }
                ?: emptyList()
            Pair(words, order)
        } catch (_: Exception) {
            Pair(emptyList(), emptyList())
        }
    }

    private fun parseMatchingPairsFromJson(json: String): Pair<List<String>, List<String>> {
        return try {
            val map: Map<String, Any> = gson.fromJson(
                json,
                object : TypeToken<Map<String, Any>>() {}.type
            )
            val pairs = map["pairs"] as? List<*> ?: emptyList<Any>()
            val leftItems = mutableListOf<String>()
            val rightItems = mutableListOf<String>()
            for (pair in pairs) {
                @Suppress("UNCHECKED_CAST")
                val pairMap = pair as? Map<String, String> ?: continue
                leftItems.add(pairMap["left"] ?: "")
                rightItems.add(pairMap["right"] ?: "")
            }
            Pair(leftItems, rightItems)
        } catch (_: Exception) {
            Pair(emptyList(), emptyList())
        }
    }

    private fun parseStringListFromJson(json: String): List<String> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}

data class LessonWithQuestions(
    val lesson: Lesson,
    val questions: List<Question>
)
