package com.example.russianpath.presentation.screens.lesson

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.russianpath.domain.model.QuestionType
import com.example.russianpath.presentation.screens.lesson.components.*
import com.example.russianpath.presentation.theme.*

@Composable
fun LessonScreen(
    lessonId: String,
    onLessonComplete: (stars: Int, xp: Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val lesson by viewModel.lesson.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val totalQuestions by viewModel.totalQuestions.collectAsState()
    val isCorrect by viewModel.isCorrect.collectAsState()
    val showHint by viewModel.showHint.collectAsState()
    val livesRemaining by viewModel.livesRemaining.collectAsState()
    val mistakesCount by viewModel.mistakesCount.collectAsState()
    
    var userAnswer by remember { mutableStateOf("") }
    
    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }
    
    val currentQuestion = questions.getOrNull(currentIndex)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            lesson?.title ?: "Урок",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = if (totalQuestions > 0) 
                                (currentIndex + 1).toFloat() / totalQuestions 
                            else 0f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = VasilisaBlue,
                            trackColor = Color.Gray.copy(alpha = 0.2f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 24.sp)
                    }
                },
                actions = {
                    // Счетчик жизней
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        repeat(livesRemaining) {
                            Text("❤️", fontSize = 20.sp)
                        }
                        repeat(5 - livesRemaining) {
                            Text("🖤", fontSize = 20.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentQuestion != null) {
                // Область вопроса
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QuestionCard(
                        question = currentQuestion,
                        isCorrect = isCorrect,
                        showHint = showHint,
                        onAnswer = { answer ->
                            userAnswer = answer
                            viewModel.checkAnswer(answer)
                        }
                    )
                }
                
                // Обратная связь
                AnimatedVisibility(
                    visible = isCorrect != null,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    FeedbackBar(
                        isCorrect = isCorrect ?: false,
                        hintText = if (!(isCorrect ?: true)) currentQuestion?.hintText else null
                    )
                }
                
                // Кнопка продолжения
                Button(
                    onClick = {
                        if (currentIndex < totalQuestions - 1) {
                            viewModel.nextQuestion()
                        } else {
                            val stars = viewModel.completeLesson()
                            val xp = stars * 20 - mistakesCount * 2
                            onLessonComplete(stars, xp)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(56.dp),
                    enabled = isCorrect != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCorrect == true) SuccessGreen else VasilisaBlue
                    )
                ) {
                    Text(
                        text = if (currentIndex < totalQuestions - 1) "Далее →" else "Завершить урок ✓",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FeedbackBar(isCorrect: Boolean, hintText: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) SuccessGreen.copy(alpha = 0.1f) 
                           else ErrorRed.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isCorrect) "✅ Отлично!" else "❌ Неправильно",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) SuccessGreen else ErrorRed
            )
            
            if (!isCorrect && hintText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 Подсказка: $hintText",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
