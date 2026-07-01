package com.example.russianpath.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.russianpath.data.repository.TopicRepository
import com.example.russianpath.data.repository.UserRepository
import com.example.russianpath.domain.model.Topic
import com.example.russianpath.domain.model.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()
    
    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()
    
    private val _mascotMessage = MutableStateFlow("Привет! Я Кнопа, давай учиться!")
    val mascotMessage: StateFlow<String> = _mascotMessage.asStateFlow()
    
    init {
        loadUserStats()
        loadTopics()
        updateMascotMessage()
    }
    
    private fun loadUserStats() {
        viewModelScope.launch {
            userRepository.getUserStats().collect { stats ->
                stats?.let { _userStats.value = it }
            }
        }
    }
    
    private fun loadTopics() {
        viewModelScope.launch {
            // Загружаем темы для 5 класса (по умолчанию)
            topicRepository.getTopicsByGrade(5).collect { topicList ->
                _topics.value = topicList
            }
        }
    }
    
    private fun updateMascotMessage() {
        viewModelScope.launch {
            userRepository.getUserStats().collect { stats ->
                stats?.let {
                    val message = when {
                        it.currentStreak >= 7 -> "Ты занимаешься уже ${it.currentStreak} дней! Супер!"
                        it.livesCount <= 2 -> "Осторожно! Осталось мало жизней!"
                        it.totalLessonsCompleted == 0 -> "Давай начнём наше приключение!"
                        else -> "Продолжай в том же духе!"
                    }
                    _mascotMessage.value = message
                }
            }
        }
    }
}
