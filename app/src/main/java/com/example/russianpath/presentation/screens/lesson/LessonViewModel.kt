package com.example.russianpath.presentation.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.russianpath.data.local.dao.LessonCompletionDao
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.entity.LessonEntity
import com.example.russianpath.data.local.entity.QuestionEntity
import com.example.russianpath.data.repository.UserRepository
import com.example.russianpath.domain.model.Lesson
import com.example.russianpath.domain.model.LessonType
import com.example.russianpath.domain.model.Question
import com.example.russianpath.domain.model.QuestionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel для экрана прохождения урока.
 *
 * Управляет:
 * - Загрузкой урока и вопросов
 * - Навигацией по вопросам
 * - Проверкой ответов
 * - Подсчётом статистики попытки
 * - Сохранением результатов
 */
@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonDao: LessonDao,
    private val questionDao: QuestionDao,
    private val lessonCompletionDao: LessonCompletionDao,
    private val userRepository: UserRepository
) : ViewModel() {

    private val gson = Gson()

    // ========================================================================
    // Состояния UI
    // ========================================================================

    private val _lesson = MutableStateFlow<Lesson?>(null)
    val lesson: StateFlow<Lesson?> = _lesson.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions: StateFlow<Int> = _totalQuestions.asStateFlow()

    private val _mistakesCount = MutableStateFlow(0)
    val mistakesCount: StateFlow<Int> = _mistakesCount.asStateFlow()

    private val _correctAnswersCount = MutableStateFlow(0)
    val correctAnswersCount: StateFlow<Int> = _correctAnswersCount.asStateFlow()

    private val _isCorrect = MutableStateFlow<Boolean?>(null)
    val isCorrect: StateFlow<Boolean?> = _isCorrect.asStateFlow()

    private val _showHint = MutableStateFlow(false)
    val showHint: StateFlow<Boolean> = _showHint.asStateFlow()

    private val _livesRemaining = MutableStateFlow(5)
    val livesRemaining: StateFlow<Int> = _livesRemaining.asStateFlow()

    private val _isLessonCompleted = MutableStateFlow(false)
    val isLessonCompleted: StateFlow<Boolean> = _isLessonCompleted.asStateFlow()

    private val _earnedStars = MutableStateFlow(0)
    val earnedStars: StateFlow<Int> = _earnedStars.asStateFlow()

    private val _earnedXp = MutableStateFlow(0)
    val earnedXp: StateFlow<Int> = _earnedXp.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Список ID вопросов, на которые были даны неправильные ответы (для аналитики)
    private val mistakesList = mutableListOf<MistakeRecord>()

    private var lessonStartTime: Long = 0L

    // ========================================================================
    // Загрузка урока
    // ========================================================================

    /**
     * Загружает урок и его вопросы по ID.
     */
    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                lessonStartTime = System.currentTimeMillis()

                val lessonEntity = withContext(Dispatchers.IO) {
                    lessonDao.getById(lessonId)
                }

                if (lessonEntity == null) {
                    _errorMessage.value = "Урок не найден"
                    _isLoading.value = false
                    return@launch
                }

                _lesson.value = lessonEntity.toDomainModel()

                // Загружаем вопросы
                questionDao.observeByLessonOrdered(lessonId).collect { questionEntities ->
                    val mappedQuestions = withContext(Dispatchers.Default) {
                        questionEntities.map { it.toDomainModel() }
                    }
                    _questions.value = mappedQuestions
                    _totalQuestions.value = mappedQuestions.size
                    _isLoading.value = false
                }

                // Загружаем текущее количество жизней
                val stats = userRepository.getUserStats()
                _livesRemaining.value = stats.livesCount

            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки урока: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // ========================================================================
    // Проверка ответов
    // ========================================================================

    /**
     * Проверяет ответ пользователя на текущий вопрос.
     *
     * @param userAnswer Ответ пользователя (строка или список).
     */
    fun checkAnswer(userAnswer: Any) {
        val question = _questions.value.getOrNull(_currentQuestionIndex.value) ?: return

        val isAnswerCorrect = question.checkAnswer(userAnswer)

        _isCorrect.value = isAnswerCorrect

        if (isAnswerCorrect) {
            _correctAnswersCount.value += 1
        } else {
            _mistakesCount.value += 1
            _livesRemaining.value = maxOf(0, _livesRemaining.value - 1)

            // Записываем ошибку для аналитики
            mistakesList.add(
                MistakeRecord(
                    questionId = question.id,
                    skillId = question.primarySkillId,
                    userAnswer = userAnswer.toString(),
                    correctAnswer = question.correctAnswer
                )
            )

            // Если закончились жизни — завершаем урок
            if (_livesRemaining.value <= 0) {
                completeLesson()
            }
        }
    }

    /**
     * Переход к следующему вопросу.
     */
    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value += 1
            _isCorrect.value = null
            _showHint.value = false
        } else {
            // Это был последний вопрос — завершаем урок
            completeLesson()
        }
    }

    // ========================================================================
    // Подсказки
    // ========================================================================

    /**
     * Показывает подсказку за самоцветы.
     */
    fun showHint() {
        viewModelScope.launch {
            val success = userRepository.spendGems(HINT_COST)
            if (success) {
                _showHint.value = true
            } else {
                _errorMessage.value = "Недостаточно самоцветов для подсказки. " +
                        "Нужно $HINT_COST, у вас ${userRepository.getGemsBalance()}"
            }
        }
    }

    /**
     * Возвращает текущий вопрос.
     */
    fun getCurrentQuestion(): Question? {
        return _questions.value.getOrNull(_currentQuestionIndex.value)
    }

    // ========================================================================
    // Завершение урока
    // ========================================================================

    /**
     * Завершает урок и сохраняет результаты.
     *
     * @return Количество заработанных звёзд.
     */
    fun completeLesson(): Int {
        if (_isLessonCompleted.value) return _earnedStars.value

        val lessonData = _lesson.value ?: return 0
        val totalQuestions = _totalQuestions.value
        val correctAnswers = _correctAnswersCount.value
        val mistakes = _mistakesCount.value

        if (totalQuestions == 0) return 0

        // Вычисляем процент правильных ответов
        val scorePercent = if (totalQuestions > 0) {
            (correctAnswers * 100) / totalQuestions
        } else {
            0
        }

        // Вычисляем звёзды
        val stars = lessonData.calculateStars(scorePercent)

        // Вычисляем XP
        val xpEarned = lessonData.calculateXpReward(scorePercent)

        // Время прохождения
        val timeSpent = ((System.currentTimeMillis() - lessonStartTime) / 1000).toInt()

        // Проверяем, пройден ли урок
        val isPassed = lessonData.isPassed(scorePercent)

        // Формируем JSON с ошибками для аналитики
        val mistakesJson = gson.toJson(mistakesList.map { it.toJsonMap() })

        _earnedStars.value = stars
        _earnedXp.value = xpEarned
        _isLessonCompleted.value = true

        // Сохраняем результат
        viewModelScope.launch {
            try {
                userRepository.completeLesson(
                    lessonId = lessonData.id,
                    topicId = lessonData.topicId,
                    stars = stars,
                    scorePercent = scorePercent,
                    correctAnswers = correctAnswers,
                    totalQuestions = totalQuestions,
                    mistakesCount = mistakes,
                    mistakesJson = mistakesJson,
                    timeSpentSeconds = timeSpent,
                    xpEarned = xpEarned,
                    gemsEarned = if (stars == lessonData.maxStars) lessonData.gemsReward else 0,
                    isPassed = isPassed
                )
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сохранения результатов: ${e.message}"
            }
        }

        return stars
    }

    /**
     * Возвращает результат урока для экрана ResultScreen.
     */
    fun getLessonResult(): LessonResult {
        return LessonResult(
            lessonTitle = _lesson.value?.title ?: "",
            stars = _earnedStars.value,
            xpEarned = _earnedXp.value,
            scorePercent = if (_totalQuestions.value > 0) {
                (_correctAnswersCount.value * 100) / _totalQuestions.value
            } else 0,
            correctAnswers = _correctAnswersCount.value,
            totalQuestions = _totalQuestions.value,
            timeSpentSeconds = ((System.currentTimeMillis() - lessonStartTime) / 1000).toInt()
        )
    }

    /**
     * Сбрасывает сообщение об ошибке.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    // ========================================================================
    // Маппинг Entity → Domain
    // ========================================================================

    /**
     * Преобразует LessonEntity в доменную модель Lesson.
     */
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

    /**
     * Преобразует QuestionEntity в доменную модель Question.
     *
     * Поддерживает все типы вопросов через парсинг dataJson.
     */
    private fun QuestionEntity.toDomainModel(): Question {
        val type = QuestionType.fromString(questionType)

        return when (type) {
            QuestionType.SINGLE_CHOICE -> {
                val options: List<String> = parseOptionsFromDataJson()
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
                val options: List<String> = parseOptionsFromDataJson()
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
                val dragData = parseDragDataFromJson()
                Question(
                    id = id,
                    lessonId = lessonId,
                    primarySkillId = primarySkillId,
                    questionType = type,
                    promptText = promptText,
                    draggableWords = dragData.words,
                    correctOrder = dragData.order,
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
                val pairs = parseMatchingPairsFromJson()
                Question(
                    id = id,
                    lessonId = lessonId,
                    primarySkillId = primarySkillId,
                    questionType = type,
                    promptText = promptText,
                    options = pairs.leftItems,
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

    /**
     * Парсит список опций из dataJson.
     * Ожидает формат: ["option1", "option2", ...]
     */
    private fun parseOptionsFromDataJson(): List<String> {
        return try {
            gson.fromJson(dataJson, object : TypeToken<List<String>>() {}.type)
                ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Парсит данные для drag-and-drop вопросов.
     * Ожидает формат: {"words": [...], "correct_order": [...]}
     */
    private fun parseDragDataFromJson(): DragData {
        return try {
            val map: Map<String, Any> = gson.fromJson(
                dataJson,
                object : TypeToken<Map<String, Any>>() {}.type
            )
            val words = (map["words"] as? List<*>)?.map { it.toString() } ?: emptyList()
            val order = (map["correct_order"] as? List<*>)
                ?.map { (it as? Double)?.toInt() ?: 0 }
                ?: emptyList()
            DragData(words, order)
        } catch (_: Exception) {
            DragData(emptyList(), emptyList())
        }
    }

    /**
     * Парсит пары для вопросов на сопоставление.
     * Ожидает формат: {"pairs": [{"left": "...", "right": "..."}, ...]}
     */
    private fun parseMatchingPairsFromJson(): MatchingPairs {
        return try {
            val map: Map<String, Any> = gson.fromJson(
                dataJson,
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
            MatchingPairs(leftItems, rightItems)
        } catch (_: Exception) {
            MatchingPairs(emptyList(), emptyList())
        }
    }

    /**
     * Парсит JSON-строку в список строк.
     */
    private fun parseStringListFromJson(json: String): List<String> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    // ========================================================================
    // Внутренние классы
    // ========================================================================

    private data class DragData(
        val words: List<String>,
        val order: List<Int>
    )

    private data class MatchingPairs(
        val leftItems: List<String>,
        val rightItems: List<String>
    )

    private data class MistakeRecord(
        val questionId: String,
        val skillId: String,
        val userAnswer: String,
        val correctAnswer: String
    ) {
        fun toJsonMap(): Map<String, String> {
            return mapOf(
                "questionId" to questionId,
                "skillId" to skillId,
                "userAnswer" to userAnswer,
                "correctAnswer" to correctAnswer
            )
        }
    }

    companion object {
        /** Стоимость подсказки в самоцветах. */
        const val HINT_COST = 10
    }
}

/**
 * Результат прохождения урока для передачи в ResultScreen.
 */
data class LessonResult(
    val lessonTitle: String,
    val stars: Int,
    val xpEarned: Int,
    val scorePercent: Int,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val timeSpentSeconds: Int
)
