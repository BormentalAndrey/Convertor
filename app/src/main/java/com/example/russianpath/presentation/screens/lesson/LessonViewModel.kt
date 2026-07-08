// app/src/main/java/com/example/russianpath/presentation/screens/lesson/LessonViewModel.kt

package com.example.russianpath.presentation.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.russianpath.data.repository.LessonRepository
import com.example.russianpath.data.repository.UserRepository
import com.example.russianpath.domain.model.Lesson
import com.example.russianpath.domain.model.Question
import com.example.russianpath.domain.model.QuestionType
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel для экрана прохождения урока.
 *
 * Управляет:
 * - Загрузкой урока и вопросов (через LessonRepository)
 * - Навигацией по вопросам
 * - Проверкой ответов (полиморфная, через domain Question.checkAnswer)
 * - Подсчётом статистики попытки
 * - Сохранением результатов (через UserRepository)
 * - Перемешиванием вариантов ответов для объективности
 *
 * Все операции ввода-вывода выполняются на IO-диспетчере.
 * Состояния UI предоставляются через StateFlow для реактивного обновления.
 */
@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
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

    /**
     * Список ошибок, допущенных в текущей попытке.
     * Каждая ошибка привязана к ID вопроса и ID навыка для аналитики.
     */
    private val mistakesList = mutableListOf<MistakeRecord>()

    /**
     * Время начала урока в миллисекундах (System.currentTimeMillis).
     * Используется для вычисления общего времени прохождения.
     */
    private var lessonStartTime: Long = 0L

    // ========================================================================
    // Загрузка урока
    // ========================================================================

    /**
     * Загружает урок и его вопросы по ID.
     *
     * Использует LessonRepository.getLessonWithQuestions() для получения
     * урока вместе с вопросами и прогрессом пользователя.
     *
     * Для вопросов типа SINGLE_CHOICE и MULTIPLE_CHOICE
     * варианты ответов автоматически перемешиваются.
     *
     * @param lessonId Уникальный идентификатор урока.
     */
    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                lessonStartTime = System.currentTimeMillis()

                // Сбрасываем состояние предыдущего урока
                resetState()

                // Загружаем урок с вопросами через репозиторий
                val lessonWithQuestions = withContext(Dispatchers.IO) {
                    lessonRepository.getLessonWithQuestions(lessonId)
                }

                if (lessonWithQuestions == null) {
                    _errorMessage.value = "Урок не найден. Возможно, он был удалён или ещё не загружен."
                    _isLoading.value = false
                    return@launch
                }

                _lesson.value = lessonWithQuestions.lesson
                
                // Перемешиваем варианты ответов для вопросов с выбором
                val shuffledQuestions = lessonWithQuestions.questions.map { question ->
                    shuffleQuestionOptions(question)
                }
                _questions.value = shuffledQuestions
                _totalQuestions.value = shuffledQuestions.size
                _isLoading.value = false

                // Загружаем текущее количество жизней пользователя
                val stats = userRepository.getUserStats()
                _livesRemaining.value = stats.livesCount

            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки урока: ${e.message}. Пожалуйста, попробуйте снова."
                _isLoading.value = false
            }
        }
    }

    /**
     * Перемешивает варианты ответов в вопросе для объективности.
     * 
     * Для SINGLE_CHOICE и MULTIPLE_CHOICE перемешивает порядок options.
     * Правильный ответ остаётся тем же, но его позиция меняется.
     * Для остальных типов вопросов (TEXT_INPUT, DICTATION и др.)
     * возвращает вопрос без изменений.
     *
     * @param question Исходный вопрос из базы данных.
     * @return Вопрос с перемешанными вариантами ответов.
     */
    private fun shuffleQuestionOptions(question: Question): Question {
        return when (question.questionType) {
            QuestionType.SINGLE_CHOICE -> {
                if (question.options.isEmpty()) return question
                
                // Перемешиваем варианты ответов
                val shuffledOptions = question.options.shuffled()
                
                question.copy(options = shuffledOptions)
            }
            QuestionType.MULTIPLE_CHOICE -> {
                if (question.options.isEmpty()) return question
                
                // Перемешиваем варианты ответов
                val shuffledOptions = question.options.shuffled()
                
                question.copy(options = shuffledOptions)
            }
            // Для остальных типов не перемешиваем
            QuestionType.TEXT_INPUT,
            QuestionType.FILL_IN_BLANK,
            QuestionType.DICTATION,
            QuestionType.WORD_DRAG,
            QuestionType.SEQUENCE_ORDER,
            QuestionType.MATCHING,
            QuestionType.STRESS_SELECTION,
            QuestionType.MORPHEMIC_ANALYSIS -> question
        }
    }

    /**
     * Сбрасывает состояние ViewModel перед загрузкой нового урока.
     */
    private fun resetState() {
        _currentQuestionIndex.value = 0
        _mistakesCount.value = 0
        _correctAnswersCount.value = 0
        _isCorrect.value = null
        _showHint.value = false
        _isLessonCompleted.value = false
        _earnedStars.value = 0
        _earnedXp.value = 0
        mistakesList.clear()
    }

    // ========================================================================
    // Проверка ответов
    // ========================================================================

    /**
     * Проверяет ответ пользователя на текущий вопрос.
     *
     * Использует полиморфный метод Question.checkAnswer(), который
     * поддерживает все типы вопросов (SINGLE_CHOICE, TEXT_INPUT, DRAG_ORDER и т.д.).
     *
     * При неправильном ответе:
     * - Увеличивает счётчик ошибок
     * - Уменьшает количество жизней
     * - Записывает ошибку в mistakesList для аналитики
     * - Если жизни закончились — автоматически завершает урок
     *
     * @param userAnswer Ответ пользователя. Тип зависит от типа вопроса:
     *   - String для SINGLE_CHOICE, TEXT_INPUT, FILL_IN_BLANK, DICTATION
     *   - List<String> для MULTIPLE_CHOICE
     *   - List<Int> для SEQUENCE_ORDER, WORD_DRAG
     *   - Map<String, String> для MATCHING
     */
    fun checkAnswer(userAnswer: Any) {
        val question = _questions.value.getOrNull(_currentQuestionIndex.value)
        if (question == null) {
            _errorMessage.value = "Вопрос не найден. Пожалуйста, перезапустите урок."
            return
        }

        val isAnswerCorrect = question.checkAnswer(userAnswer)

        _isCorrect.value = isAnswerCorrect

        if (isAnswerCorrect) {
            _correctAnswersCount.value += 1
        } else {
            _mistakesCount.value += 1
            _livesRemaining.value = maxOf(0, _livesRemaining.value - 1)

            // Записываем ошибку для аналитики с привязкой к навыку
            mistakesList.add(
                MistakeRecord(
                    questionId = question.id,
                    skillId = question.primarySkillId,
                    userAnswer = userAnswer.toString(),
                    correctAnswer = question.correctAnswer
                )
            )

            // Если закончились жизни — автоматически завершаем урок
            if (_livesRemaining.value <= 0) {
                completeLesson()
            }
        }
    }

    /**
     * Переход к следующему вопросу.
     *
     * Если это был последний вопрос — автоматически завершает урок.
     */
    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value += 1
            _isCorrect.value = null
            _showHint.value = false
        } else {
            completeLesson()
        }
    }

    /**
     * Переход к предыдущему вопросу.
     * Используется для режима свободной навигации.
     */
    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
            _isCorrect.value = null
            _showHint.value = false
        }
    }

    /**
     * Переход к конкретному вопросу по индексу.
     *
     * @param index Индекс вопроса (0-based).
     */
    fun goToQuestion(index: Int) {
        if (index in _questions.value.indices) {
            _currentQuestionIndex.value = index
            _isCorrect.value = null
            _showHint.value = false
        }
    }

    // ========================================================================
    // Подсказки
    // ========================================================================

    /**
     * Показывает подсказку за самоцветы.
     *
     * Списывает HINT_COST самоцветов через UserRepository.spendGems().
     * Если самоцветов недостаточно — показывает сообщение об ошибке.
     * Если списание успешно — показывает подсказку.
     */
    fun showHint() {
        viewModelScope.launch {
            val success = userRepository.spendGems(HINT_COST)
            if (success) {
                _showHint.value = true
            } else {
                val currentBalance = userRepository.getGemsBalance()
                _errorMessage.value = "Недостаточно самоцветов для подсказки. " +
                        "Нужно $HINT_COST 💎, у вас $currentBalance 💎. " +
                        "Пройдите больше уроков, чтобы заработать самоцветы!"
            }
        }
    }

    /**
     * Скрывает подсказку.
     */
    fun hideHint() {
        _showHint.value = false
    }

    /**
     * Возвращает текущий вопрос.
     *
     * @return Текущий Question или null, если вопросы ещё не загружены.
     */
    fun getCurrentQuestion(): Question? {
        return _questions.value.getOrNull(_currentQuestionIndex.value)
    }

    /**
     * Проверяет, можно ли перейти к следующему вопросу.
     * Пользователь должен ответить на текущий вопрос перед переходом.
     */
    fun canProceedToNext(): Boolean {
        return _isCorrect.value != null
    }

    // ========================================================================
    // Завершение урока
    // ========================================================================

    /**
     * Завершает урок и сохраняет результаты.
     *
     * Вычисляет:
     * - Процент правильных ответов
     * - Количество звёзд (через Lesson.calculateStars)
     * - Награду XP (через Lesson.calculateXpReward)
     * - Время прохождения
     * - Статус прохождения (зачёт/незачёт)
     *
     * Сохраняет результат через UserRepository.completeLesson().
     * Идемпотентен: повторный вызов не дублирует сохранение.
     *
     * @return Количество заработанных звёзд (0–maxStars).
     */
    fun completeLesson(): Int {
        if (_isLessonCompleted.value) return _earnedStars.value

        val lessonData = _lesson.value ?: return 0
        val totalQuestions = _totalQuestions.value
        val correctAnswers = _correctAnswersCount.value
        val mistakes = _mistakesCount.value

        if (totalQuestions == 0) {
            _errorMessage.value = "Нет вопросов для завершения урока."
            return 0
        }

        // Вычисляем процент правильных ответов
        val scorePercent = (correctAnswers * 100) / totalQuestions

        // Вычисляем звёзды на основе процента и порога прохождения
        val stars = lessonData.calculateStars(scorePercent)

        // Вычисляем XP с учётом процента правильных ответов
        val xpEarned = lessonData.calculateXpReward(scorePercent)

        // Время прохождения в секундах
        val timeSpent = ((System.currentTimeMillis() - lessonStartTime) / 1000).toInt()

        // Проверяем, пройден ли урок (достигнут ли порог)
        val isPassed = lessonData.isPassed(scorePercent)

        // Награда самоцветами — только за идеальное прохождение
        val gemsEarned = if (stars == lessonData.maxStars && scorePercent >= 100) {
            lessonData.gemsReward
        } else {
            0
        }

        // Формируем JSON с ошибками для аналитики
        val mistakesJson = gson.toJson(mistakesList.map { it.toJsonMap() })

        _earnedStars.value = stars
        _earnedXp.value = xpEarned
        _isLessonCompleted.value = true

        // Сохраняем результат в репозиторий
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
                    gemsEarned = gemsEarned,
                    isPassed = isPassed
                )
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка сохранения результатов: ${e.message}. " +
                        "Результат будет сохранён при следующей синхронизации."
            }
        }

        return stars
    }

    /**
     * Возвращает результат урока для передачи в ResultScreen.
     *
     * @return LessonResult с полной статистикой попытки.
     */
    fun getLessonResult(): LessonResult {
        val totalQuestions = _totalQuestions.value
        val correctAnswers = _correctAnswersCount.value
        val timeSpent = if (lessonStartTime > 0) {
            ((System.currentTimeMillis() - lessonStartTime) / 1000).toInt()
        } else {
            0
        }

        return LessonResult(
            lessonTitle = _lesson.value?.title ?: "",
            stars = _earnedStars.value,
            xpEarned = _earnedXp.value,
            scorePercent = if (totalQuestions > 0) {
                (correctAnswers * 100) / totalQuestions
            } else {
                0
            },
            correctAnswers = correctAnswers,
            totalQuestions = totalQuestions,
            timeSpentSeconds = timeSpent
        )
    }

    /**
     * Сбрасывает сообщение об ошибке после его отображения в UI.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    // ========================================================================
    // Внутренние классы
    // ========================================================================

    /**
     * Запись об ошибке для аналитики.
     *
     * @property questionId ID вопроса, на который дан неправильный ответ.
     * @property skillId ID микро-навыка, к которому относится вопрос.
     * @property userAnswer Ответ пользователя.
     * @property correctAnswer Правильный ответ.
     */
    private data class MistakeRecord(
        val questionId: String,
        val skillId: String,
        val userAnswer: String,
        val correctAnswer: String
    ) {
        /**
         * Преобразует запись в Map для сериализации в JSON.
         * Формат: {"questionId": "...", "skillId": "...", "userAnswer": "...", "correctAnswer": "..."}
         */
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
 *
 * @property lessonTitle Название урока.
 * @property stars Количество заработанных звёзд (0–3).
 * @property xpEarned Количество заработанного опыта.
 * @property scorePercent Процент правильных ответов (0–100).
 * @property correctAnswers Количество правильных ответов.
 * @property totalQuestions Общее количество вопросов в уроке.
 * @property timeSpentSeconds Время прохождения в секундах.
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
