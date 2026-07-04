package com.example.russianpath.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.russianpath.data.repository.LessonRepository
import com.example.russianpath.data.repository.TopicRepository
import com.example.russianpath.data.repository.UserRepository
import com.example.russianpath.domain.model.Topic
import com.example.russianpath.domain.model.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel для главного экрана (Dashboard).
 *
 * Управляет:
 * - Статистикой пользователя (через UserRepository)
 * - Списком тем с прогрессом (через TopicRepository + LessonRepository)
 * - Сообщениями маскота (контекстные, на основе статистики)
 * - Сменой класса (с сохранением выбора)
 * - Обновлением стрика
 *
 * Все данные предоставляются через StateFlow для реактивного обновления UI.
 * Тяжёлые операции (обогащение тем прогрессом) выполняются на IO-диспетчере.
 *
 * @see TopicRepository
 * @see UserRepository
 * @see LessonRepository
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val userRepository: UserRepository,
    private val lessonRepository: LessonRepository
) : ViewModel() {

    // ========================================================================
    // Состояния UI
    // ========================================================================

    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _mascotMessage = MutableStateFlow("Привет! Я Кнопа, давай учиться!")
    val mascotMessage: StateFlow<String> = _mascotMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _currentGradeId = MutableStateFlow("5")
    val currentGradeId: StateFlow<String> = _currentGradeId.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Комбинированное состояние "пустого" экрана.
     * true, когда загрузка завершена и список тем пуст.
     */
    val isEmpty: StateFlow<Boolean> = combine(
        _topics,
        _isLoading
    ) { topics, isLoading ->
        !isLoading && topics.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadUserStats()
        loadUserDefaultGrade()
        observeMascotMessage()
    }

    // ========================================================================
    // Загрузка данных
    // ========================================================================

    /**
     * Загружает статистику пользователя и реактивно обновляет UI.
     * Использует Flow для автоматического обновления при изменениях в БД.
     */
    private fun loadUserStats() {
        viewModelScope.launch {
            try {
                userRepository.observeUserStats().collect { stats ->
                    _userStats.value = stats
                }
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось загрузить статистику: ${e.message}"
            }
        }
    }

    /**
     * Загружает темы для указанного класса и обогащает их данными о прогрессе.
     *
     * Для каждой темы вычисляется:
     * - completionPercentage — процент завершённых уроков
     * - stars — сумма звёзд за все уроки темы
     * - totalLessons — общее количество уроков в теме
     * - completedLessons — количество пройденных уроков
     *
     * @param gradeId ID класса (например, "5", "11", "oge", "ege").
     */
    private fun loadTopics(gradeId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                topicRepository.observeTopicsByGrade(gradeId).collect { topicList ->
                    val enrichedTopics = withContext(Dispatchers.IO) {
                        enrichTopicsWithProgress(topicList)
                    }
                    _topics.value = enrichedTopics
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось загрузить темы: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Обогащает список тем данными о прогрессе пользователя.
     *
     * Алгоритм для каждой темы:
     * 1. Загружает все уроки темы с прогрессом через LessonRepository
     * 2. Подсчитывает количество завершённых уроков (isCompleted = true)
     * 3. Вычисляет процент завершения и сумму звёзд
     *
     * Если для какой-то темы не удалось загрузить прогресс —
     * возвращает тему без изменений (не блокирует отображение).
     *
     * @param topics Список тем для обогащения.
     * @return Список тем с заполненными полями прогресса.
     */
    private suspend fun enrichTopicsWithProgress(topics: List<Topic>): List<Topic> {
        return topics.map { topic ->
            try {
                val lessonsWithQuestions = lessonRepository.getLessonsWithQuestionsByTopic(topic.id)

                val totalLessons = lessonsWithQuestions.size
                val completedLessons = lessonsWithQuestions.count { it.lesson.isCompleted }
                val completionPercentage = if (totalLessons > 0) {
                    (completedLessons.toFloat() / totalLessons * 100).coerceIn(0f, 100f)
                } else {
                    0f
                }
                val stars = lessonsWithQuestions.sumOf { it.lesson.bestStars }

                topic.copy(
                    completionPercentage = completionPercentage,
                    stars = stars,
                    totalLessons = totalLessons,
                    completedLessons = completedLessons
                )
            } catch (_: Exception) {
                // Если не удалось загрузить прогресс для конкретной темы —
                // возвращаем тему как есть, не блокируя отображение остальных
                topic
            }
        }
    }

    /**
     * Загружает темы для класса пользователя по умолчанию.
     *
     * Если пользователь уже выбрал класс ранее (сохранён в UserProgress) —
     * использует его. Иначе используется класс по умолчанию ("5").
     */
    fun loadUserDefaultGrade() {
        viewModelScope.launch {
            val savedGradeId = withContext(Dispatchers.IO) {
                userRepository.getCurrentGradeId()
            }
            if (savedGradeId.isNotBlank()) {
                _currentGradeId.value = savedGradeId
            }
            loadTopics(_currentGradeId.value)
        }
    }

    /**
     * Переключает класс и перезагружает темы.
     *
     * Сохраняет выбор пользователя в UserProgress для восстановления
     * при следующем запуске приложения.
     *
     * @param gradeId ID класса (например, "5", "11", "oge", "ege").
     */
    fun switchGrade(gradeId: String) {
        if (gradeId == _currentGradeId.value) return
        _currentGradeId.value = gradeId

        // Сохраняем выбор класса в фоне
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.updateCurrentPosition(gradeId, "")
            }
        }

        loadTopics(gradeId)
    }

    /**
     * Принудительно обновляет все данные (pull-to-refresh).
     * Перезагружает темы для текущего класса.
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                // Перезагружаем темы
                loadTopics(_currentGradeId.value)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // ========================================================================
    // Логика маскота
    // ========================================================================

    /**
     * Реактивно обновляет сообщение маскота на основе статистики пользователя.
     * Сообщение меняется автоматически при изменении любого показателя.
     */
    private fun observeMascotMessage() {
        viewModelScope.launch {
            userRepository.observeUserStats().collect { stats ->
                _mascotMessage.value = generateMascotMessage(stats)
            }
        }
    }

    /**
     * Генерирует контекстное сообщение маскота на основе статистики.
     *
     * Приоритет сообщений (от высшего к низшему):
     * 1. Приветствие нового пользователя (0 уроков)
     * 2. Достижения по стрику (30+, 14+, 7+, 3+ дней)
     * 3. Предупреждения о жизнях (1, 2-3 жизни)
     * 4. Обратная связь по точности (<60%, ≥95%)
     * 5. Поздравление с высоким уровнем (20+)
     * 6. Случайное мотивационное сообщение
     *
     * @param stats Текущая статистика пользователя.
     * @return Сообщение для отображения в UI.
     */
    private fun generateMascotMessage(stats: UserStats): String {
        return when {
            // Приветствие нового пользователя
            stats.totalLessonsCompleted == 0 -> {
                "Привет! Я Кнопа, твой помощник в изучении русского языка! " +
                        "Давай начнём наше приключение — выбери первую тему!"
            }

            // Достижения по стрику
            stats.currentStreak >= 30 -> {
                "🔥 Потрясающе! Ты занимаешься уже ${stats.currentStreak} " +
                        getStreakDaysWord(stats.currentStreak) + " подряд! " +
                        "Ты — настоящий герой русского языка!"
            }
            stats.currentStreak >= 14 -> {
                "🚀 Две недели без перерыва! " +
                        "Ты на пути к званию Мастера слова!"
            }
            stats.currentStreak >= 7 -> {
                "💪 Отличная работа! ${stats.currentStreak} " +
                        getStreakDaysWord(stats.currentStreak) + " непрерывных занятий!"
            }
            stats.currentStreak >= 3 -> {
                "👍 Ты занимаешься ${stats.currentStreak} " +
                        getStreakDaysWord(stats.currentStreak) + " подряд. Так держать!"
            }

            // Предупреждения о жизнях
            stats.livesCount <= 1 -> {
                "⚠️ Осторожно! У тебя осталась всего ${stats.livesCount} жизнь. " +
                        "Будь внимателен в следующем уроке! " +
                        "Жизни восстанавливаются со временем."
            }
            stats.livesCount <= 3 -> {
                "📉 У тебя осталось ${stats.livesCount} жизни. " +
                        "Пора восстановить силы! Подожди немного или заработай самоцветы."
            }

            // Обратная связь по точности (только после минимум 5 уроков)
            stats.totalLessonsCompleted >= 5 && stats.accuracy < 60f -> {
                "📚 Не расстраивайся! Ошибки — это часть обучения. " +
                        "Давай повторим сложные темы вместе! " +
                        "Твоя точность: ${stats.accuracy.toInt()}%"
            }
            stats.totalLessonsCompleted >= 10 && stats.accuracy >= 95f -> {
                "🏆 Впечатляющая точность ${stats.accuracy.toInt()}%! " +
                        "Ты почти не допускаешь ошибок!"
            }

            // Высокий уровень
            stats.level >= 20 -> {
                "👑 Уровень ${stats.level}! " +
                        "Ты уже ${stats.getLevelTitle()}! " +
                        "Осталось ${stats.xpToNextLevel} XP до следующего уровня."
            }

            // Случайное мотивационное сообщение
            else -> {
                val messages = listOf(
                    "📖 Каждый день — новый шаг к мастерству!",
                    "🌟 Русский язык полон секретов. Давай раскроем их вместе!",
                    "💎 Практика — ключ к успеху. Выбери следующую тему!",
                    "🎪 Учиться может быть весело! Попробуй бонусный урок.",
                    "🔍 Я заметил, ты становишься лучше с каждым днём!",
                    "🎯 Помни: идеальный результат — не главное. Главное — прогресс!",
                    "🦉 Мудрость приходит с практикой. Продолжай в том же духе!",
                    "🌈 После дождя всегда появляется радуга. Не сдавайся после ошибок!"
                )
                messages[stats.totalLessonsCompleted % messages.size]
            }
        }
    }

    // ========================================================================
    // Действия пользователя
    // ========================================================================

    /**
     * Обрабатывает нажатие на тему.
     *
     * Логика:
     * - Если тема не разблокирована и есть пререквизиты — сообщение о необходимости пройти их.
     * - Если тема не разблокирована без пререквизитов — сообщение о скором открытии.
     * - Если тема разблокирована — возвращает true (навигация разрешена).
     *
     * @param topic Тема, на которую нажал пользователь.
     * @return true если навигация разрешена, false если тема заблокирована.
     */
    fun onTopicClick(topic: Topic): Boolean {
        if (!topic.isUnlocked) {
            val prerequisites = topic.prerequisiteTopicIds
            if (prerequisites.isNotEmpty()) {
                _mascotMessage.value = "🔒 Эта тема пока закрыта. " +
                        "Сначала пройди предыдущие темы этого раздела!"
            } else {
                _mascotMessage.value = "🔒 Эта тема скоро откроется! " +
                        "Продолжай изучать текущие темы."
            }
            return false
        }
        return true
    }

    /**
     * Сбрасывает сообщение об ошибке после его отображения в UI.
     * Вызывается после показа Snackbar или диалога с ошибкой.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Обновляет стрик при открытии приложения.
     *
     * Вызывается из Activity или NavGraph при старте приложения.
     * После обновления стрика запускает refresh() для актуализации статистики.
     */
    fun onAppOpened() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    userRepository.updateStreak()
                }
                // Обновляем статистику и темы после обновления стрика
                refresh()
            } catch (_: Exception) {
                // Стрик — некритичная операция.
                // Если не удалось обновить — продолжаем работу с текущими данными.
            }
        }
    }

    // ========================================================================
    // Утилиты
    // ========================================================================

    /**
     * Возвращает правильное склонение слова "день" для числительного.
     *
     * Правила русской грамматики:
     * - 1 день (кроме 11)
     * - 2, 3, 4 дня (кроме 12, 13, 14)
     * - 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15... дней
     *
     * @param days Количество дней.
     * @return Строка "день", "дня" или "дней".
     */
    private fun getStreakDaysWord(days: Int): String {
        val lastDigit = days % 10
        val lastTwoDigits = days % 100
        return when {
            lastTwoDigits in 11..14 -> "дней"
            lastDigit == 1 -> "день"
            lastDigit in 2..4 -> "дня"
            else -> "дней"
        }
    }
}
