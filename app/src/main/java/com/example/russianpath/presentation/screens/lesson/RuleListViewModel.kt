// app/src/main/java/com/example/russianpath/presentation/screens/lesson/RuleListViewModel.kt

package com.example.russianpath.presentation.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.russianpath.data.repository.RuleRepository
import com.example.russianpath.data.repository.TopicRepository
import com.example.russianpath.domain.model.Rule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RuleListViewModel @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val topicRepository: TopicRepository
) : ViewModel() {

    private val _rules = MutableStateFlow<List<Rule>>(emptyList())
    val rules: StateFlow<List<Rule>> = _rules.asStateFlow()

    private val _topicTitle = MutableStateFlow("")
    val topicTitle: StateFlow<String> = _topicTitle.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadRules(topicId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val topic = withContext(Dispatchers.IO) {
                    topicRepository.getTopicById(topicId)
                }
                _topicTitle.value = "📖 ${topic?.title ?: "Правила"}"

                ruleRepository.observeRulesByTopic(topicId).collect { ruleList ->
                    _rules.value = ruleList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки правил: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}
