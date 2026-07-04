// app/src/main/java/com/example/russianpath/presentation/screens/lesson/LessonScreen.kt

package com.example.russianpath.presentation.screens.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.russianpath.domain.model.Question
import com.example.russianpath.domain.model.QuestionType
import com.example.russianpath.presentation.components.Emoji
import com.example.russianpath.presentation.components.EmojiText
import com.example.russianpath.presentation.theme.ErrorRed
import com.example.russianpath.presentation.theme.SuccessGreen
import com.example.russianpath.presentation.theme.VasilisaBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    onBackClick: () -> Unit = {},
    onComplete: (LessonResult) -> Unit = {},
    viewModel: LessonViewModel = hiltViewModel()
) {
    val lesson by viewModel.lesson.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val totalQuestions by viewModel.totalQuestions.collectAsStateWithLifecycle()
    val isCorrect by viewModel.isCorrect.collectAsStateWithLifecycle()
    val showHint by viewModel.showHint.collectAsStateWithLifecycle()
    val livesRemaining by viewModel.livesRemaining.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isLessonCompleted by viewModel.isLessonCompleted.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) { viewModel.loadLesson(lessonId) }
    LaunchedEffect(isLessonCompleted) {
        if (isLessonCompleted) onComplete(viewModel.getLessonResult())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = lesson?.title ?: "Урок", fontWeight = FontWeight.Bold, maxLines = 1)
                        if (totalQuestions > 0) {
                            LinearProgressIndicator(
                                progress = (currentQuestionIndex + 1).toFloat() / totalQuestions,
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = VasilisaBlue,
                                trackColor = Color.Gray.copy(alpha = 0.2f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { EmojiText(Emoji.BACK, fontSize = 24) }
                },
                actions = {
                    Row(modifier = Modifier.padding(end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        repeat(livesRemaining) { EmojiText(Emoji.HEART, fontSize = 18) }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "$livesRemaining",
                            fontWeight = FontWeight.Bold,
                            color = if (livesRemaining <= 2) ErrorRed else Color.DarkGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VasilisaBlue)
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⚠️", fontSize = 48.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(text = errorMessage ?: "", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { viewModel.loadLesson(lessonId) }) {
                            Text("Повторить загрузку")
                        }
                    }
                }
            }
            questions.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📭", fontSize = 48.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(text = "Список вопросов пуст", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Text(text = "ID урока: $lessonId", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { viewModel.loadLesson(lessonId) }) {
                            Text("Загрузить снова")
                        }
                    }
                }
            }
            else -> {
                val question = viewModel.getCurrentQuestion()
                if (question != null) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuestionCard(question = question, showHint = showHint, onAnswer = { viewModel.checkAnswer(it) }, isAnswered = isCorrect != null)

                        AnimatedVisibility(visible = isCorrect != null, enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically()) {
                            FeedbackCard(
                                isCorrect = isCorrect ?: false,
                                explanation = if (isCorrect == true) question.explanationText.ifBlank { "Отлично! Ты справился!" }
                                else question.hintText.ifBlank { "Неправильно. Правильный ответ: ${question.correctAnswer}" }
                            )
                        }

                        ActionButtons(
                            isAnswered = isCorrect != null,
                            isLastQuestion = currentQuestionIndex >= totalQuestions - 1,
                            onNext = { viewModel.nextQuestion() },
                            onHint = { viewModel.showHint() },
                            hintAvailable = !showHint && isCorrect == null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionCard(question: Question, showHint: Boolean, onAnswer: (Any) -> Unit, isAnswered: Boolean) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = question.promptText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))

            when (question.questionType) {
                QuestionType.SINGLE_CHOICE -> SingleChoiceOptions(options = question.options, onSelect = { onAnswer(it) }, enabled = !isAnswered)
                QuestionType.MULTIPLE_CHOICE -> MultipleChoiceOptions(options = question.options, onSelectionChanged = { onAnswer(it) }, enabled = !isAnswered)
                QuestionType.TEXT_INPUT, QuestionType.FILL_IN_BLANK, QuestionType.DICTATION -> TextInputField(onSubmit = { onAnswer(it) }, enabled = !isAnswered)
                QuestionType.WORD_DRAG, QuestionType.SEQUENCE_ORDER -> DragOrderOptions(words = question.draggableWords, onOrderChanged = { onAnswer(it) }, enabled = !isAnswered)
                QuestionType.MATCHING -> MatchingOptions(leftItems = question.options, rightItems = question.acceptableAnswers, onMatch = { onAnswer(it) }, enabled = !isAnswered)
                QuestionType.STRESS_SELECTION -> StressSelectionOptions(word = question.promptText, onSelect = { onAnswer(it) }, enabled = !isAnswered)
                QuestionType.MORPHEMIC_ANALYSIS -> MorphemicAnalysisInput(onSubmit = { onAnswer(it) }, enabled = !isAnswered)
            }

            if (showHint) {
                Spacer(Modifier.height(16.dp))
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = VasilisaBlue.copy(alpha = 0.1f))) {
                    Text(
                        text = "💡 Подсказка: ${question.hintText.ifBlank { question.explanationText }}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleChoiceOptions(options: List<String>, onSelect: (String) -> Unit, enabled: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            Button(onClick = { onSelect(option) }, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = enabled, shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface)) {
                Text(text = option, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun MultipleChoiceOptions(options: List<String>, onSelectionChanged: (List<String>) -> Unit, enabled: Boolean) {
    var selected by remember { mutableStateOf<Set<String>>(emptySet()) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = option in selected
            OutlinedButton(onClick = { selected = if (isSelected) selected - option else selected + option; onSelectionChanged(selected.toList()) },
                modifier = Modifier.fillMaxWidth().height(56.dp), enabled = enabled, shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (isSelected) VasilisaBlue.copy(alpha = 0.1f) else Color.Transparent)) {
                Text(text = option, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun TextInputField(onSubmit: (String) -> Unit, enabled: Boolean) {
    var text by remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = text, onValueChange = { text = it }, enabled = enabled, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Введи ответ...") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onSubmit(text) }, enabled = enabled && text.isNotBlank(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Проверить") }
    }
}

@Composable
private fun DragOrderOptions(words: List<String>, onOrderChanged: (List<Int>) -> Unit, enabled: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        words.forEachIndexed { index, word ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${index + 1}.", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text(text = word, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun MatchingOptions(leftItems: List<String>, rightItems: List<String>, onMatch: (Map<String, String>) -> Unit, enabled: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        leftItems.forEachIndexed { index, left ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = left, fontWeight = FontWeight.Bold)
                    Text(text = rightItems.getOrElse(index) { "?" }, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun StressSelectionOptions(word: String, onSelect: (String) -> Unit, enabled: Boolean) {
    Text(text = "Выбери ударную гласную в слове: $word", style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun MorphemicAnalysisInput(onSubmit: (String) -> Unit, enabled: Boolean) {
    Text(text = "Разбери слово по составу (приставка, корень, суффикс, окончание)", style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun FeedbackCard(isCorrect: Boolean, explanation: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isCorrect) SuccessGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            EmojiText(emoji = if (isCorrect) Emoji.CHECK else Emoji.CROSS, fontSize = 28)
            Spacer(Modifier.width(12.dp))
            Text(text = if (isCorrect) "Правильно!" else "Неправильно", fontWeight = FontWeight.Bold, color = if (isCorrect) SuccessGreen else ErrorRed)
            if (explanation.isNotBlank()) {
                Spacer(Modifier.width(8.dp))
                Text(text = explanation, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray, maxLines = 3)
            }
        }
    }
}

@Composable
private fun ActionButtons(isAnswered: Boolean, isLastQuestion: Boolean, onNext: () -> Unit, onHint: () -> Unit, hintAvailable: Boolean) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (hintAvailable) {
            OutlinedButton(onClick = onHint, modifier = Modifier.fillMaxWidth()) { Text("💡 Подсказка (10 💎)") }
        }
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = isAnswered, shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VasilisaBlue)) {
            Text(text = if (isLastQuestion) "Завершить ${Emoji.CHECK}" else "Далее ${Emoji.FORWARD}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
