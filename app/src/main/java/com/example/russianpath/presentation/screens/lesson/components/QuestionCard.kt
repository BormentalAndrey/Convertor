package com.example.russianpath.presentation.screens.lesson.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianpath.domain.model.Question
import com.example.russianpath.domain.model.QuestionType
// ИСПРАВЛЕНО: Импортируем все вложенные элементы разметки вопросов, если они разнесены по файлам
import com.example.russianpath.presentation.screens.lesson.components.*

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
                    DragOrderQuestion(
                        words = question.draggableWords,
                        correctOrder = question.correctOrder,
                        isAnswered = isCorrect != null,
                        onSubmit = onAnswer
                    )
                }
                
                QuestionType.AUDIO -> {
                    AudioQuestion(
                        audioPath = question.audioPath,
                        options = question.options,
                        correctAnswer = question.correctAnswer,
                        isAnswered = isCorrect != null,
                        onSelect = onAnswer
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
