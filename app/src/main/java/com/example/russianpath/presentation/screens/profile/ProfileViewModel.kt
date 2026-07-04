// app/src/main/java/com/example/russianpath/presentation/screens/profile/ProfileViewModel.kt

package com.example.russianpath.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.russianpath.data.repository.UserRepository
import com.example.russianpath.domain.model.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана профиля пользователя.
 *
 * Управляет:
 * - Отображением статистики пользователя
 * - Обновлением данных профиля
 *
 * Все данные предоставляются через StateFlow для реактивного обновления UI.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUserStats()
    }

    /**
     * Загружает статистику пользователя.
     */
    private fun loadUserStats() {
        viewModelScope.launch {
            try {
                userRepository.observeUserStats().collect { stats ->
                    _userStats.value = stats
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось загрузить профиль: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Принудительно обновляет данные профиля.
     */
    fun refresh() {
        viewModelScope.launch {
            try {
                val stats = userRepository.getUserStats()
                _userStats.value = stats
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось обновить профиль: ${e.message}"
            }
        }
    }

    /**
     * Сбрасывает сообщение об ошибке.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
