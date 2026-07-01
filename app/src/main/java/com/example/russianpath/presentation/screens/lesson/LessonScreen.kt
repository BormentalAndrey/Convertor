package com.example.russianpath.presentation.screens.lesson

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.russianpath.R
import com.example.russianpath.domain.model.QuestionType
import com.example.russianpath.presentation.components.LivesDisplay
import com.example.russianpath.presentation.screens.lesson.components.*
import com.example.russianpath.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
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
                        Image(
                            painter = painterResource(R.drawable.ic_lock),
                            contentDescription = "Назад",
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                actions = {
                    LivesDisplay(
                        currentLives = livesRemaining,
                        maxLives = 5,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentQuestion != null) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
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
                    
                    // Кнопка подсказки
                    if (isCorrect == null) {
                        TextButton(
                            onClick = { viewModel.showHint() },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_hint),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Подсказка (-10 💎)")
                        }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(
                        if (isCorrect) R.drawable.ic_check else R.drawable.ic_cross
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCorrect) "Отлично!" else "Неправильно",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) SuccessGreen else ErrorRed
                )
            }
            
            if (!isCorrect && hintText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Image(
                        painter = painterResource(R.drawable.ic_hint),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Подсказка: $hintText",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
