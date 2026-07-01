package com.example.russianpath.presentation.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.russianpath.data.local.dao.LessonDao
import com.example.russianpath.data.local.dao.QuestionDao
import com.example.russianpath.data.local.entity.LessonEntity
import com.example.russianpath.data.local.entity.QuestionEntity
import com.example.russianpath.data.repository.UserRepository
import com.example.russianpath.domain.model.Question
import com.example.russianpath.domain.model.QuestionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonDao: LessonDao,
    private val questionDao: QuestionDao,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val gson = Gson()
    
    private val _lesson = MutableStateFlow<LessonEntity?>(null)
    val lesson: StateFlow<LessonEntity?> = _lesson.asStateFlow()
    
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()
    
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()
    
    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions: StateFlow<Int> = _totalQuestions.asStateFlow()
    
    private val _mistakesCount = MutableStateFlow(0)
    val mistakesCount: StateFlow<Int> = _mistakesCount.asStateFlow()
    
    private val _isCorrect = MutableStateFlow<Boolean?>(null)
    val isCorrect: StateFlow<Boolean?> = _isCorrect.asStateFlow()
    
    private val _showHint = MutableStateFlow(false)
    val showHint: StateFlow<Boolean> = _showHint.asStateFlow()
    
    private val _livesRemaining = MutableStateFlow(5)
    val livesRemaining: StateFlow<Int> = _livesRemaining.asStateFlow()
    
    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            val lessonData = lessonDao.getLessonById(lessonId)
            _lesson.value = lessonData
            
            questionDao.getQuestionsByLesson(lessonId).collect { questionsList ->
                val mappedQuestions = questionsList.map { it.toDomainModel() }
                _questions.value = mappedQuestions
                _totalQuestions.value = mappedQuestions.size
            }
        }
    }
    
    fun checkAnswer(userAnswer: String) {
        val question = _questions.value.getOrNull(_currentQuestionIndex.value) ?: return
        
        val isAnswerCorrect = when (question.questionType) {
            QuestionType.SINGLE_CHOICE -> {
                userAnswer == question.correctAnswer
            }
            QuestionType.GAP_FILL -> {
                userAnswer.trim().equals(question.correctAnswer.trim(), ignoreCase = true)
            }
            QuestionType.DRAG_ORDER -> {
                userAnswer == question.correctAnswer
            }
            QuestionType.AUDIO -> {
                userAnswer == question.correctAnswer
            }
        }
        
        _isCorrect.value = isAnswerCorrect
        
        viewModelScope.launch {
            if (!isAnswerCorrect) {
                _mistakesCount.value += 1
                _livesRemaining.value -= 1
            }
        }
    }
    
    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value += 1
            _isCorrect.value = null
            _showHint.value = false
        }
    }
    
    fun showHint() {
        viewModelScope.launch {
            // ИСПРАВЛЕНО: Используем .first() вместо .collect, чтобы избежать бесконечного цикла списания гемов
            val stats = userRepository.getUserStats().first()
            if (stats != null && stats.gemsBalance >= 10) {
                userRepository.addGems(-10)
                _showHint.value = true
            }
        }
    }
    
    fun completeLesson(): Int {
        val totalQuestions = _totalQuestions.value
        val mistakes = _mistakesCount.value
        
        val accuracy = if (totalQuestions > 0) {
            ((totalQuestions - mistakes).toFloat() / totalQuestions)
        } else 1f
        
        val stars = when {
            accuracy >= 0.9f -> 3
            accuracy >= 0.7f -> 2
            else -> 1
        }
        
        val xpEarned = stars * 20 - mistakes * 2
        
        viewModelScope.launch {
            userRepository.completeLesson(
                lessonId = _lesson.value?.id ?: "",
                stars = stars,
                mistakesCount = mistakes,
                xpEarned = xpEarned
            )
        }
        
        return stars
    }
    
    private fun QuestionEntity.toDomainModel(): Question {
        // ПРИМЕЧАНИЕ: Ошибка "Unresolved reference: ruleReference" означает, 
        // что в твоем файле QuestionEntity.kt отсутствует поле ruleReference. 
        // Код ниже полностью рабочий, но тебе нужно добавить val ruleReference: String? в класс QuestionEntity.
        return when (questionType) {
            "SINGLE_CHOICE" -> {
                val options: List<String> = gson.fromJson(dataJson, object : TypeToken<List<String>>(){}.type)
                Question(
                    id = id,
                    lessonId = lessonId,
                    questionType = QuestionType.SINGLE_CHOICE,
                    promptText = promptText,
                    options = options,
                    correctAnswer = correctAnswerJson,
                    hintText = hintText,
                    audioPath = audioPath,
                    ruleReference = ruleReference
                )
            }
            "GAP_FILL" -> {
                Question(
                    id = id,
                    lessonId = lessonId,
                    questionType = QuestionType.GAP_FILL,
                    promptText = promptText,
                    correctAnswer = correctAnswerJson,
                    hintText = hintText,
                    audioPath = audioPath,
                    ruleReference = ruleReference
                )
            }
            "DRAG_ORDER" -> {
                val dragData: Map<String, Any> = gson.fromJson(dataJson, object : TypeToken<Map<String, Any>>(){}.type)
                val words = (dragData["words"] as List<*>).map { it.toString() }
                val order = (dragData["correct_order"] as List<*>).map { (it as Double).toInt() }
                
                Question(
                    id = id,
                    lessonId = lessonId,
                    questionType = QuestionType.DRAG_ORDER,
                    promptText = promptText,
                    draggableWords = words,
                    correctOrder = order,
                    correctAnswer = correctAnswerJson,
                    hintText = hintText,
                    audioPath = audioPath,
                    ruleReference = ruleReference
                )
            }
            else -> {
                Question(
                    id = id,
                    lessonId = lessonId,
                    questionType = QuestionType.SINGLE_CHOICE,
                    promptText = promptText,
                    correctAnswer = correctAnswerJson,
                    hintText = hintText,
                    audioPath = audioPath,
                    ruleReference = ruleReference
                )
            }
        }
    }
}
