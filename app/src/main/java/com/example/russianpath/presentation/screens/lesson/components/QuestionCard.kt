// app/src/main/java/com/example/russianpath/presentation/screens/lesson/components/QuestionCard.kt

package com.example.russianpath.presentation.screens.lesson.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.russianpath.domain.model.Question
import com.example.russianpath.domain.model.QuestionType

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
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = question.promptText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when (question.questionType) {
                QuestionType.SINGLE_CHOICE,
                QuestionType.MULTIPLE_CHOICE -> {
                    SingleChoiceQuestion(
                        options = question.options,
                        correctAnswer = question.correctAnswer,
                        isAnswered = isCorrect != null,
                        onSelect = onAnswer
                    )
                }
                QuestionType.TEXT_INPUT,
                QuestionType.FILL_IN_BLANK,
                QuestionType.DICTATION -> {
                    GapFillQuestion(
                        correctAnswer = question.correctAnswer,
                        isAnswered = isCorrect != null,
                        onSubmit = onAnswer
                    )
                }
                QuestionType.WORD_DRAG,
                QuestionType.SEQUENCE_ORDER -> {
                    Text(
                        text = "Расставь по порядку: ${question.draggableWords.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                QuestionType.MATCHING -> {
                    Text(
                        text = "Сопоставь пары: ${question.options.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                QuestionType.STRESS_SELECTION -> {
                    Text(
                        text = "Выбери ударную гласную: ${question.options.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                QuestionType.MORPHEMIC_ANALYSIS -> {
                    Text(
                        text = "Разбери слово по составу: ${question.promptText}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            if (showHint && question.ruleReference.isNotBlank()) {
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
