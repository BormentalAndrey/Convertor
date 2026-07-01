package com.example.russianpath.presentation.screens.lesson.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.russianpath.domain.model.Question
import com.example.russianpath.domain.model.QuestionType

// ИСПРАВЛЕНО: Добавлены импорты для глобальных компонентов
import com.example.russianpath.presentation.components.DragOrderQuestion
import com.example.russianpath.presentation.components.AudioQuestion

@Composable
fun QuestionCard(
    question: Question,
    isCorrect: Boolean?,
    showHint: Boolean,
    onAnswer: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Текст вопроса
            Text(
                text = question.promptText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Разные типы вопросов
            when (question.questionType) {
                QuestionType.SINGLE_CHOICE -> {
                    SingleChoiceQuestion(
                        options = question.options,
                        correctAnswer = question.correctAnswer,
                        isAnswered = isCorrect != null,
                        onSelect = onAnswer
                    )
                }
                
                QuestionType.GAP_FILL -> {
                    GapFillQuestion(
                        correctAnswer = question.correctAnswer,
                        isAnswered = isCorrect != null,
                        onSubmit = onAnswer
                    )
                }
                
                QuestionType.DRAG_ORDER -> {
                    // ИСПРАВЛЕНО: Адаптировано под новую сигнатуру DragOrderQuestion
                    DragOrderQuestion(
                        questionText = "Расставь по порядку:",
                        shuffledParts = question.draggableWords,
                        onAnswerReady = { parts -> 
                            onAnswer(parts.joinToString(",")) 
                        }
                    )
                }
                
                QuestionType.AUDIO -> {
                    // ИСПРАВЛЕНО: Адаптировано под новую сигнатуру AudioQuestion
                    AudioQuestion(
                        questionText = "Прослушай и выбери правильный ответ:",
                        options = question.options,
                        onPlayAudio = { 
                            // TODO: Воспроизвести question.audioPath (пока заглушка для компилятора)
                        },
                        onAnswerSelected = { selected ->
                            onAnswer(selected)
                        }
                    )
                }
            }
            
            // Правило-подсказка
            if (showHint && question.ruleReference != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = question.ruleReference,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
