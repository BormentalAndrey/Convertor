package com.example.russianpath.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.russianpath.data.repository.TopicRepository
import com.example.russianpath.data.repository.UserRepository
import com.example.russianpath.domain.model.Topic
import com.example.russianpath.domain.model.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для главного экрана (Dashboard).
 *
 * Управляет:
 * - Статистикой пользователя
 * - Списком тем
 * - Сообщениями маскота
 * - Сменой класса
 *
 * Все данные предоставляются через StateFlow для реактивного обновления UI.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val userRepository: UserRepository
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

    private val _currentGradeId = MutableStateFlow("5")
    val currentGradeId: StateFlow<String> = _currentGradeId.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Комбинированное состояние "пустого" экрана
    val isEmpty: StateFlow<Boolean> = combine(
        _topics,
        _isLoading
    ) { topics, isLoading ->
        !isLoading && topics.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadUserStats()
        loadTopics()
        observeMascotMessage()
    }

    // ========================================================================
    // Загрузка данных
    // ========================================================================

    /**
     * Загружает статистику пользователя и обновляет UI.
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
     * Загружает темы для текущего класса.
     */
    private fun loadTopics() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                topicRepository.observeTopicsByGrade(_currentGradeId.value).collect { topicList ->
                    _topics.value = topicList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось загрузить темы: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Переключает класс и перезагружает темы.
     *
     * @param gradeId ID класса (например, "5", "11", "oge", "ege").
     */
    fun switchGrade(gradeId: String) {
        if (gradeId == _currentGradeId.value) return
        _currentGradeId.value = gradeId
        viewModelScope.launch {
            userRepository.updateCurrentPosition(gradeId, "")
        }
        loadTopics()
    }

    /**
     * Загружает темы для класса пользователя по умолчанию.
     * Вызывается, если пользователь уже выбрал класс ранее.
     */
    fun loadUserDefaultGrade() {
        viewModelScope.launch {
            val savedGradeId = userRepository.getCurrentGradeId()
            if (savedGradeId.isNotBlank()) {
                _currentGradeId.value = savedGradeId
                loadTopics()
            }
        }
    }

    // ========================================================================
    // Логика маскота
    // ========================================================================

    /**
     * Реактивно обновляет сообщение маскота на основе статистики.
     */
    private fun observeMascotMessage() {
        viewModelScope.launch {
            userRepository.observeUserStats().collect { stats ->
                _mascotMessage.value = generateMascotMessage(stats)
            }
        }
    }

    /**
     * Генерирует сообщение маскота в зависимости от показателей пользователя.
     */
    private fun generateMascotMessage(stats: UserStats): String {
        return when {
            stats.totalLessonsCompleted == 0 -> {
                "Привет! Я Кнопа, твой помощник в изучении русского языка! " +
                        "Давай начнём наше приключение — выбери первую тему!"
            }
            stats.currentStreak >= 30 -> {
                "🔥 Потрясающе! Ты занимаешься уже ${stats.currentStreak} дней подряд! " +
                        "Ты — настоящий герой русского языка!"
            }
            stats.currentStreak >= 14 -> {
                "🚀 Две недели без перерыва! Ты на пути к званию Мастера слова!"
            }
            stats.currentStreak >= 7 -> {
                "💪 Отличная работа! ${stats.currentStreak} дней непрерывных занятий!"
            }
            stats.currentStreak >= 3 -> {
                "👍 Ты занимаешься ${stats.currentStreak} дня подряд. Так держать!"
            }
            stats.livesCount <= 1 -> {
                "⚠️ Осторожно! У тебя осталась всего ${stats.livesCount} жизнь. " +
                        "Будь внимателен в следующем уроке!"
            }
            stats.livesCount <= 3 -> {
                "📉 У тебя осталось ${stats.livesCount} жизни. Пора восстановить силы!"
            }
            stats.accuracy < 60f -> {
                "📚 Не расстраивайся! Ошибки — это часть обучения. " +
                        "Давай повторим сложные темы вместе!"
            }
            stats.accuracy >= 95f -> {
                "🏆 Впечатляющая точность ${stats.accuracy.toInt()}%! " +
                        "Продолжай в том же духе!"
            }
            stats.level > stats.totalLessonsCompleted / 10 -> {
                "🎯 Твой уровень растёт быстрее обычного! Отличный прогресс!"
            }
            else -> {
                val messages = listOf(
                    "📖 Каждый день — новый шаг к мастерству!",
                    "🌟 Русский язык полон секретов. Давай раскроем их вместе!",
                    "💎 Практика — ключ к успеху. Выбери следующую тему!",
                    "🎪 Учиться может быть весело! Попробуй бонусный урок.",
                    "🔍 Я заметил, ты становишься лучше с каждым днём!"
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
     * Если тема не разблокирована — показывает сообщение.
     * Если разблокирована — возвращает true (навигация).
     */
    fun onTopicClick(topic: Topic): Boolean {
        if (!topic.isUnlocked) {
            val prerequisites = topic.prerequisiteTopicIds
            if (prerequisites.isNotEmpty()) {
                _mascotMessage.value = "🔒 Эта тема пока закрыта. " +
                        "Сначала пройди предыдущие темы этого раздела!"
            } else {
                _mascotMessage.value = "🔒 Эта тема скоро откроется!"
            }
            return false
        }
        return true
    }

    /**
     * Сбрасывает сообщение об ошибке после его отображения.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Обновляет стрик при открытии приложения.
     * Вызывается из Activity или главного экрана.
     */
    fun onAppOpened() {
        viewModelScope.launch {
            userRepository.updateStreak()
        }
    }
}
